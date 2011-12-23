/*******************************************************************************
 * Copyright (c) 2011 Ale46.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
import java.awt.EventQueue;
import java.util.ArrayList;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileFilter;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JTextArea;
import javax.swing.JButton;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

import jgroove.JGroovex;
import javax.swing.ListSelectionModel;


public class GrooveMyTunes extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	static DefaultListModel<Object> modelLocal = new DefaultListModel<Object>();
	static DefaultListModel<Object> modelMatched = new DefaultListModel<Object>();
	static JTextArea textArea;
	JList<Object> list,list_1;
	static ArrayList<GrooveSong> toAdd;
	ArrayList<Song> noMatch;
	static ArrayList<String> local;
	private static String playlistName;

	/**
	 * Launch the application.
	 * @throws UnsupportedLookAndFeelException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws ClassNotFoundException 
	 */
	public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		EventQueue.invokeLater(new Runnable() {
			
			public void run() {
				try {
					GrooveMyTunes frame = new GrooveMyTunes();

					frame.setVisible(true);

					new Thread(new Runnable(){

						@SuppressWarnings("unchecked")
						@Override
						public void run() {
							JFileChooser jFileChooser = new JFileChooser();
							jFileChooser.setFileFilter(new XmlFilter());
							jFileChooser.showOpenDialog(null);
							File f = jFileChooser.getSelectedFile();
							playlistName = (f.getName().substring(0,f.getName().length()-4));
							GrooveImporter gi  = new GrooveImporter(f.getAbsolutePath());
							

							try {
								ExecutorService executor = Executors.newSingleThreadExecutor();
								
								Future<ArrayList<Object>> task = executor.submit(gi);
								
								ArrayList<Object> res = task.get();
								toAdd = (ArrayList<GrooveSong>) res.get(0);

								System.out.println("Matched:"+toAdd.size());
								local =  (ArrayList<String>) res.get(1);
								
								

							
								for (int i = 0;i<toAdd.size();i++)
								{


									//modelLocal.add(i, test[i].getName() + " - " +  test[i].getArtist());
									modelMatched.add(i, toAdd.get(i).songName+" - " + toAdd.get(i).artistName);
									modelLocal.add(i,local.get(i));
								}

/*								Iterator<Song> iterator = local.iterator();
								int count = 0;
								while (iterator.hasNext()){
									Song song = iterator.next();
									modelLocal.add(count,song.getName() + " - "+song.getArtist());
									count++;
								}*/
							} catch (Exception e) {

							}

						}

					}).start();

				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});
	}

	/**
	 * Create the frame.
	 */
	public GrooveMyTunes() {
		setTitle("GrooveMyTunes");
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 734, 545);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);

		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(null, "Local Songs", TitledBorder.CENTER, TitledBorder.TOP, null, null));

		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new TitledBorder(null, "Matched Songs", TitledBorder.CENTER, TitledBorder.TOP, null, null));

		JButton btnDeleteMatch = new JButton("Delete Match");
		btnDeleteMatch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int sel = list.getSelectedIndex();
				modelLocal.remove(sel);
				modelMatched.remove(sel);
				toAdd.remove(sel);
			}
		});

		JButton btnStartImporting = new JButton("Start Importing");
		btnStartImporting.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				textArea.append("Login please wait..\n");
				GrooveMyTunes.textArea.setCaretPosition(GrooveMyTunes.textArea.getDocument().getLength());
				try {
					
					String user  = JOptionPane.showInputDialog ( "Enter username" );
					String password  = JOptionPane.showInputDialog ( "Enter password" );
					
					if(!user.isEmpty() && !password.isEmpty()){
						JGroovex.authenticateUser(user,password);
						List<String> iwillAdd = new ArrayList<String>();
						for (int k = 0;k<toAdd.size();k++){
							iwillAdd.add( toAdd.get(k).songID );
							textArea.append(toAdd.get(k).songID + " " +toAdd.get(k).songName + " - "+ toAdd.get(k).albumName + " addedd..\n");
							GrooveMyTunes.textArea.setCaretPosition(GrooveMyTunes.textArea.getDocument().getLength());


						}
						JGroovex.createPlaylist(playlistName, "Imported playlist", iwillAdd);
						JOptionPane.showMessageDialog(null, "Playlist Imported");
					}else{
						JOptionPane.showMessageDialog(null, "Error");
					}
					
				} catch (IOException e2) {

					e2.printStackTrace();
				}


			}

		});

		JScrollPane scrollPane = new JScrollPane();
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 695, GroupLayout.PREFERRED_SIZE)
						.addGroup(Alignment.TRAILING, gl_contentPane.createSequentialGroup()
							.addComponent(panel, GroupLayout.DEFAULT_SIZE, 305, Short.MAX_VALUE)
							.addGap(18)
							.addComponent(btnDeleteMatch)
							.addGap(18)
							.addComponent(panel_1, GroupLayout.PREFERRED_SIZE, 269, GroupLayout.PREFERRED_SIZE))
						.addComponent(btnStartImporting, Alignment.TRAILING))
					.addGap(15))
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_contentPane.createSequentialGroup()
									.addGap(3)
									.addComponent(panel_1, GroupLayout.PREFERRED_SIZE, 348, Short.MAX_VALUE))
								.addComponent(panel, Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 351, Short.MAX_VALUE))
							.addGap(18)
							.addComponent(btnStartImporting))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGap(152)
							.addComponent(btnDeleteMatch)))
					.addGap(20)
					.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 86, GroupLayout.PREFERRED_SIZE))
		);
		
				textArea = new JTextArea();
				scrollPane.setViewportView(textArea);
				
						textArea.setWrapStyleWord(true);
						textArea.setLineWrap(true);
						textArea.setEditable(false);
		
		JScrollPane scrollPane_2 = new JScrollPane();
		
		JScrollPane scrollPane_3 = new JScrollPane();
		GroupLayout gl_panel_1 = new GroupLayout(panel_1);
		gl_panel_1.setHorizontalGroup(
			gl_panel_1.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_1.createSequentialGroup()
					.addGroup(gl_panel_1.createParallelGroup(Alignment.TRAILING)
						.addGroup(Alignment.LEADING, gl_panel_1.createSequentialGroup()
							.addGap(28)
							.addComponent(scrollPane_2, GroupLayout.DEFAULT_SIZE, 346, Short.MAX_VALUE))
						.addGroup(Alignment.LEADING, gl_panel_1.createSequentialGroup()
							.addGap(116)
							.addComponent(scrollPane_3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
					.addContainerGap())
		);
		gl_panel_1.setVerticalGroup(
			gl_panel_1.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_panel_1.createSequentialGroup()
					.addContainerGap()
					.addComponent(scrollPane_2, GroupLayout.PREFERRED_SIZE, 314, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(scrollPane_3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		
				list_1 = new JList<Object>(modelMatched);
				scrollPane_2.setViewportView(list_1);
				list_1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		panel_1.setLayout(gl_panel_1);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		//JScrollPane scrollpane = new JScrollPane(list);
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addGap(24)
					.addComponent(scrollPane_1, GroupLayout.DEFAULT_SIZE, 243, Short.MAX_VALUE)
					.addContainerGap())
		);
		gl_panel.setVerticalGroup(
			gl_panel.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap(106, Short.MAX_VALUE)
					.addComponent(scrollPane_1, GroupLayout.PREFERRED_SIZE, 317, GroupLayout.PREFERRED_SIZE)
					.addContainerGap())
		);
		
				list = new JList<Object>(modelLocal);
				scrollPane_1.setViewportView(list);
				list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
				list.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent arg0) {
						list_1.setSelectedIndex(list.getSelectedIndex());
					}
				});
		panel.setLayout(gl_panel);
		contentPane.setLayout(gl_contentPane);


	}
}


class XmlFilter extends FileFilter {

	  public boolean accept(File file) {
	    if (file.isDirectory()) return true;
	    String fname = file.getName().toLowerCase();
	    return fname.endsWith("xml");
	  }

	  public String getDescription() {
	    return "Playlist Files (xml)";
	  }
}
