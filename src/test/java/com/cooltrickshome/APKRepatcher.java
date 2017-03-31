package com.cooltrickshome;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.DefaultCaret;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import org.apache.commons.io.FileUtils;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.fife.ui.rtextarea.SearchContext;
import org.fife.ui.rtextarea.SearchEngine;

/**
 * Shows GUI with options
 * @author csanuragjain
 * https://cooltrickshome.blogspot.com
 */
public class APKRepatcher implements Runnable {

	private DefaultMutableTreeNode root;
	private DefaultTreeModel treeModel;
	private JTree tree;
	static JTextArea consoleArea;
	static int currentTabIndex = -1, previousTabIndex = -1;
	private JTextField searchField, repWithField;
	private JCheckBox forSearch, backSearch, regexCB, matchCaseCB, wholeWord,markAll;
	private File fileRoot = null;
	static ClosableTabbedPane tabPane = null;
	static Map<String, Integer> openedTabs = new HashMap<String, Integer>();
	static JEditorPane versionChecker;
	static JMenuItem updateSoft;
	private JSplitPane splitPane = null, splitPaneInternal;
	static JButton updateAvail;
	static JFrame memoryDecider;
	static JTextField memField;
	JLabel memLabel;
	String filePath = null;
	JFrame frame = null;
	String currVersion = "1.0.0";
	String title = "APKRepatcher by csanuragjain (https://cooltrickshome.blogspot.com)";
	String howToUseURL = "https://cooltrickshome.blogspot.com/2017/03/apkrepatcher-now-decompile-recompile.html";
	String gitURL = "https://github.com/csanuragjain/APKRepatcher/releases";
	String checkVersion = "https://raw.githubusercontent.com/csanuragjain/APKRepatcher/master/VERSION";
	
	@Override
	public void run() {
		frame = new JFrame(title);
		ImageIcon imgIcon = new ImageIcon(Utility.getIconPath());
		frame.setIconImage(imgIcon.getImage());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JMenuBar menuBar = new JMenuBar();
		JMenu file = new JMenu("File");
		JMenu edit = new JMenu("Edit");
		JMenu extra = new JMenu("Extra");
		JMenu advanced = new JMenu("Advanced");
		JMenu settings = new JMenu("Settings");
		JMenu help = new JMenu("Help");
		
		JMenuItem memory = new JMenuItem("Change Memory Allocation");
		settings.add(memory);
		JMenuItem open = new JMenuItem("Open APK");
		open.setAccelerator(KeyStroke.getKeyStroke('O', Toolkit
				.getDefaultToolkit().getMenuShortcutKeyMask()));
		file.add(open);
		file.addSeparator();
		JMenuItem openProj = new JMenuItem("Open Project");
		openProj.setAccelerator(KeyStroke.getKeyStroke('P', Toolkit
				.getDefaultToolkit().getMenuShortcutKeyMask()));
		file.add(openProj);
		file.addSeparator();
		JMenuItem save = new JMenuItem("Compile & Save", KeyEvent.VK_S);
		save.setAccelerator(KeyStroke.getKeyStroke('S', Toolkit
				.getDefaultToolkit().getMenuShortcutKeyMask()));
		file.add(save);
		file.addSeparator();
		JMenuItem build = new JMenuItem("Build Apk");
		build.setAccelerator(KeyStroke.getKeyStroke('B', Toolkit
				.getDefaultToolkit().getMenuShortcutKeyMask()));
		file.add(build);
		// findDialog = new FindDialog(this, this);
		// edit.add(new JMenuItem(new ShowFindDialogAction()));
		JMenuItem incFont = new JMenuItem("Increase Code Fontsize");
		incFont.setAccelerator(KeyStroke.getKeyStroke('I', Toolkit
				.getDefaultToolkit().getMenuShortcutKeyMask()));
		edit.add(incFont);
		edit.addSeparator();
		JMenuItem decFont = new JMenuItem("Decrease Code Fontsize");
		decFont.setAccelerator(KeyStroke.getKeyStroke('D', Toolkit
				.getDefaultToolkit().getMenuShortcutKeyMask()));
		edit.add(decFont);
		edit.addSeparator();
		JMenuItem removeAllTab = new JMenuItem("Remove all tabs");
		edit.add(removeAllTab);
		JMenuItem extractAPK = new JMenuItem("Extract APK - APKTOOL");
		extra.add(extractAPK);
		extra.addSeparator();
		JMenuItem dex2Jar = new JMenuItem("Convert Dex to Jar");
		extra.add(dex2Jar);
		extra.addSeparator();
		JMenuItem dex2Class = new JMenuItem("Convert Dex to Class");
		extra.add(dex2Class);
		extra.addSeparator();
		JMenuItem jar2Dex = new JMenuItem("Convert Jar/Class to Dex");
		extra.add(jar2Dex);
		extra.addSeparator();
		JMenuItem class2Smali = new JMenuItem("Convert Class to Smali");
		extra.add(class2Smali);
		extra.addSeparator();
		JMenuItem smali2Class = new JMenuItem("Convert Smali to Class");
		extra.add(smali2Class);
		extra.addSeparator();
		JMenuItem smali2Java = new JMenuItem("Convert Smali to Java");
		extra.add(smali2Java);
		extra.addSeparator();
		JMenuItem dex2Smali = new JMenuItem("Convert Dex to Smali");
		extra.add(dex2Smali);
		extra.addSeparator();
		JMenuItem smali2Dex = new JMenuItem("Convert Smali to Dex");
		extra.add(smali2Dex);
		extra.addSeparator();
		JMenuItem dexjar2Java = new JMenuItem("Convert Dex/Jar to Java");
		extra.add(dexjar2Java);
		extra.addSeparator();
		JMenuItem signAPK = new JMenuItem("Sign your APK");
		extra.add(signAPK);

		JMenuItem generateSmali = new JMenuItem("Edit smali using current code");
		advanced.add(generateSmali);
		JMenuItem editSmali = new JMenuItem("Edit smali using original apk");
		advanced.add(editSmali);
		JMenuItem saveSmaliChange = new JMenuItem("Save & apply smali changes");
		advanced.add(saveSmaliChange);

		updateSoft = new JMenuItem("Update Software");
		help.add(updateSoft);
		JMenuItem howToUse = new JMenuItem("How to use APKRepatcher");
		help.add(howToUse);

		menuBar.add(file);
		menuBar.add(edit);
		menuBar.add(extra);
		menuBar.add(advanced);
		menuBar.add(settings);
		menuBar.add(help);

		
		memory.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				memoryDecider=new JFrame("Change Memory Allocated");
				JLabel l=new JLabel("Change APKRepatcher memory allocation to ");
				memField=new JTextField(5);
				JLabel l2=new JLabel("mb");
				JButton b=new JButton("Save");
				memField.setText("1500");
				b.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent evt) {
						try{
						Utility.memoryAllocated=Integer.parseInt(memField.getText());
						Utility.memUnit="m";
						memLabel.setText("Memory Allocated: "+Utility.memoryAllocated+Utility.memUnit);
						JOptionPane.showMessageDialog(null,"Update memory settings for this APKRepatcher instance.");
						memoryDecider.dispose();
						} catch(Exception e){
							JOptionPane.showMessageDialog(null,"Exception while saving memory settings.");
						}
					}
				});
				memoryDecider.add(l);
				memoryDecider.add(memField);
				memoryDecider.add(l2);
				memoryDecider.add(b);
				memoryDecider.setLayout(new FlowLayout(0));
				memoryDecider.setVisible(true);
				memoryDecider.setSize(500,100);
				Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
				memoryDecider.setLocation(dim.width / 2 - memoryDecider.getSize().width / 2, dim.height/ 2 - memoryDecider.getSize().height / 2);
				memoryDecider.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			}
		});
		
		
		howToUse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {

					@Override
					protected Void doInBackground() throws Exception {
						// TODO Auto-generated method stub
						try {
							ConsoleViewer.cleanConsole();
							ConsoleViewer
									.setText("Redirecting to help manual at "
											+ howToUseURL);
							java.net.URI uri = new java.net.URI(howToUseURL);
							Utility.open(uri);
						} catch (Exception e) {
							ConsoleViewer
									.setText("Issue while opening help website "
											+ howToUseURL
											+ ".Please open it manually. "
											+ e.getMessage());
							Font font = new Font(Font.SANS_SERIF, Font.PLAIN,
									16);
							JFrame f = new JFrame("URL to open");
							JEditorPane ed = new JEditorPane();
							ed.setFont(font);
							ed.setText("Java is not able to launch links on your computer.\nRequest you to kindly open below link manually \n"
									+ howToUseURL);
							f.add(ed);
							Dimension dim = Toolkit.getDefaultToolkit()
									.getScreenSize();
							f.setSize(500, 200);
							f.setLocation(
									dim.width / 2 - f.getSize().width / 2,
									dim.height / 2 - f.getSize().height / 2);
							f.setVisible(true);
							f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
						}
						return null;
					}
				};
				worker.execute();
/*				Thread t = new Thread() {
					public void run() {
						
					}
				};
				t.start();*/
			}
		});

		updateSoft.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {

					@Override
					protected Void doInBackground() throws Exception {
						try {
							ConsoleViewer.cleanConsole();
							ConsoleViewer.setText("Redirecting to update site "
									+ gitURL);
							java.net.URI uri = new java.net.URI(gitURL);
							Utility.open(uri);
						} catch (Exception e) {
							ConsoleViewer
									.setText("Issue while opening update website "
											+ gitURL
											+ ".Please open it manually. "
											+ e.getMessage());
							Font font = new Font(Font.SANS_SERIF, Font.PLAIN,
									16);
							JFrame f = new JFrame("URL to open");
							JEditorPane ed = new JEditorPane();
							ed.setFont(font);
							ed.setText("Java is not able to launch links on your computer.\nRequest you to kindly open below link manually \n"
									+ gitURL);
							f.add(ed);
							Dimension dim = Toolkit.getDefaultToolkit()
									.getScreenSize();
							f.setSize(500, 200);
							f.setLocation(
									dim.width / 2 - f.getSize().width / 2,
									dim.height / 2 - f.getSize().height / 2);
							f.setVisible(true);
							f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
						}
						return null;
					}

				};
								worker.execute();
				/*Thread t = new Thread() {
					public void run() {

					}
				};
				t.start();*/
			}
		});

		extractAPK.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {

					@Override
					protected Void doInBackground() throws Exception {
						// TODO Auto-generated method stub
						String extractingFile = "APK";
						try {
							ConsoleViewer.cleanConsole();
							FileNameExtensionFilter filter = new FileNameExtensionFilter(
									"APK Files", "apk");
							JFileChooser chooser = new JFileChooser();
							chooser.setFileFilter(filter);
							chooser.setCurrentDirectory(new File(Utility
									.getProjectPath()));
							chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
							int choice = chooser.showOpenDialog(null);
							if (choice != JFileChooser.APPROVE_OPTION)
								return null;
							File chosenFile = chooser.getSelectedFile();
							if (!chosenFile.getName().contains(".apk")) {
								JOptionPane
										.showMessageDialog(null,
												"Invalid file type. Please provide apk file");
								throw new Exception("Invalid file type");
							}
							File f = new File(chosenFile.getParentFile()
									.getAbsolutePath()
									+ File.separator
									+ "extractedAPK");
							f.mkdirs();
							Utility.extractSmaliUsingAPKTool(
									chosenFile.getAbsolutePath(),
									f.getAbsolutePath());
							ConsoleViewer.setText(extractingFile
									+ " extracted at " + f.getAbsolutePath());
							JOptionPane.showMessageDialog(null,
									"Verify console logs to confirm if process went smooth. "
											+ extractingFile + " extracted at "
											+ f.getAbsolutePath());
						} catch (Exception e) {
							JOptionPane
									.showMessageDialog(
											null,
											"Issue while extracting "
													+ extractingFile
													+ ".Please retry "
													+ e.getMessage());
						}
						return null;
					}

				};
								worker.execute();
				/*Thread t = new Thread() {
					public void run() {

					}
				};
				t.start();*/
			}
		});

		dex2Jar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {

					@Override
					protected Void doInBackground() throws Exception {
						// TODO Auto-generated method stub
						String extractingFile = "DEX";
						try {
							ConsoleViewer.cleanConsole();
							FileNameExtensionFilter filter = new FileNameExtensionFilter(
									"Dex Files", "dex");
							JFileChooser chooser = new JFileChooser();
							chooser.setFileFilter(filter);
							chooser.setName("Choose Dex File");
							chooser.setCurrentDirectory(new File(Utility
									.getProjectPath()));
							chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
							int choice = chooser.showOpenDialog(null);
							if (choice != JFileChooser.APPROVE_OPTION)
								return null;
							File chosenFile = chooser.getSelectedFile();
							if (!chosenFile.getName().contains(".dex")) {
								JOptionPane
										.showMessageDialog(null,
												"Invalid file type. Please provide dex file");
								throw new Exception("Invalid file type");
							}
							String outputFileName = chosenFile.getName()
									.replace(".dex", ".jar");
							File f = new File(chosenFile.getParentFile()
									.getAbsolutePath()
									+ File.separator
									+ outputFileName);
							ConsoleViewer.setText("Converting the provided "
									+ extractingFile + "...");
							Utility.changeDex2Jar(chosenFile, f);
							ConsoleViewer.setText(extractingFile
									+ " extracted at " + f.getAbsolutePath());
							JOptionPane.showMessageDialog(null,
									"Verify console logs to confirm if process went smooth. "
											+ extractingFile + " extracted at "
											+ f.getAbsolutePath());
						} catch (Exception e) {
							JOptionPane
									.showMessageDialog(
											null,
											"Issue while extracting "
													+ extractingFile
													+ ".Please retry "
													+ e.getMessage());
						}
						return null;
					}

				};
								worker.execute();
/*				Thread t = new Thread() {
					public void run() {

					}
				};
				t.start();*/
			}
		});

		dex2Class.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {

					@Override
					protected Void doInBackground() throws Exception {
						// TODO Auto-generated method stub
						String extractingFile = "DEX";
						try {
							ConsoleViewer.cleanConsole();
							FileNameExtensionFilter filter = new FileNameExtensionFilter(
									"Dex Files", "dex");
							JFileChooser chooser = new JFileChooser();
							chooser.setFileFilter(filter);
							chooser.setCurrentDirectory(new File(Utility
									.getProjectPath()));
							chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
							int choice = chooser.showOpenDialog(null);
							if (choice != JFileChooser.APPROVE_OPTION)
								return null;
							File chosenFile = chooser.getSelectedFile();
							if (!chosenFile.getName().contains(".dex")) {
								JOptionPane
										.showMessageDialog(null,
												"Invalid file type. Please provide dex file");
								throw new Exception("Invalid file type");
							}
							String outputFileName = chosenFile.getName()
									.replace(".dex", "");
							File f = new File(chosenFile.getParentFile()
									.getAbsolutePath()
									+ File.separator
									+ outputFileName);
							f.mkdirs();
							ConsoleViewer.setText("Converting the provided "
									+ extractingFile + "...");
							Utility.changeDex2Jar(chosenFile, f);
							ConsoleViewer.setText(extractingFile
									+ " extracted at " + f.getAbsolutePath());
							JOptionPane.showMessageDialog(null,
									"Verify console logs to confirm if process went smooth. "
											+ extractingFile + " extracted at "
											+ f.getAbsolutePath());
						} catch (Exception e) {
							JOptionPane
									.showMessageDialog(
											null,
											"Issue while extracting "
													+ extractingFile
													+ ".Please retry "
													+ e.getMessage());
						}
						return null;
					}

				};
								worker.execute();
				/*Thread t = new Thread() {
					public void run() {

					}
				};
				t.start();*/
			}
		});

		jar2Dex.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {

					@Override
					protected Void doInBackground() throws Exception {
						// TODO Auto-generated method stub
						String extractingFile = "Jar/Class";
						try {
							ConsoleViewer.cleanConsole();
							FileNameExtensionFilter filter = new FileNameExtensionFilter(
									"Jar/Class Files", "jar", "class");
							JFileChooser chooser = new JFileChooser();
							chooser.setFileFilter(filter);
							chooser.setCurrentDirectory(new File(Utility
									.getProjectPath()));
							chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
							int choice = chooser.showOpenDialog(null);
							if (choice != JFileChooser.APPROVE_OPTION)
								return null;
							File chosenFile = chooser.getSelectedFile();
							if (!chosenFile.getName().contains(".jar")
									&& !chosenFile.getName().contains(".class")) {
								JOptionPane
										.showMessageDialog(null,
												"Invalid file type. Please provide jar/class file");
								throw new Exception("Invalid file type");
							}
							String outputFileName = chosenFile.getName()
									.replace(".jar", ".dex")
									.replace(".class", ".dex");
							File f = new File(chosenFile.getParentFile()
									.getAbsolutePath()
									+ File.separator
									+ outputFileName);
							ConsoleViewer.setText("Converting the provided "
									+ extractingFile + "...");
							Utility.jar2Dex(chosenFile.getAbsolutePath(),
									f.getAbsolutePath());
							ConsoleViewer
									.setText("Verify console logs to confirm if process went smooth. "
											+ extractingFile
											+ " converted at "
											+ f.getAbsolutePath());
							JOptionPane.showMessageDialog(null, extractingFile
									+ " converted at " + f.getAbsolutePath());
						} catch (Exception e) {
							JOptionPane
									.showMessageDialog(
											null,
											"Issue while extracting "
													+ extractingFile
													+ ".Please retry "
													+ e.getMessage());
						}
						return null;
					}

				};
								worker.execute();
/*				Thread t = new Thread() {
					public void run() {

					}
				};
				t.start();*/
			}
		});

		class2Smali.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {

					@Override
					protected Void doInBackground() throws Exception {
						// TODO Auto-generated method stub
						String extractingFile = "Class";
						try {
							ConsoleViewer.cleanConsole();
							FileNameExtensionFilter filter = new FileNameExtensionFilter(
									"Class Files", "class");
							JFileChooser chooser = new JFileChooser();
							chooser.setFileFilter(filter);
							chooser.setCurrentDirectory(new File(Utility
									.getProjectPath()));
							chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
							int choice = chooser.showOpenDialog(null);
							if (choice != JFileChooser.APPROVE_OPTION)
								return null;
							File chosenFile = chooser.getSelectedFile();
							if (!chosenFile.getName().contains(".class")) {
								JOptionPane
										.showMessageDialog(null,
												"Invalid file type. Please provide class file");
								throw new Exception("Invalid file type");
							}
							String outputFileName = "temp"
									+ File.separator
									+ chosenFile.getName().replace(".class",
											".dex");
							File f = new File(chosenFile.getParentFile()
									.getAbsolutePath()
									+ File.separator
									+ outputFileName);
							f.getParentFile().mkdirs();
							ConsoleViewer.setText("Converting the provided "
									+ extractingFile + " to dex first...");
							Utility.jar2Dex(chosenFile.getAbsolutePath(),
									f.getAbsolutePath());
							ConsoleViewer.setText("Converting dex to smali");
							String outputFileName2 = chosenFile.getName()
									.replace(".class", ".smali");
							File f2 = new File(chosenFile.getParentFile()
									.getAbsolutePath()
									+ File.separator
									+ outputFileName2);
							Utility.changeDex2Smali(f, f2);
							ConsoleViewer.setText(extractingFile
									+ " converted at " + f2.getAbsolutePath());
							JOptionPane.showMessageDialog(null,
									"Verify console logs to confirm if process went smooth. "
											+ extractingFile + " converted at "
											+ f2.getAbsolutePath());
						} catch (Exception e) {
							JOptionPane
									.showMessageDialog(
											null,
											"Issue while extracting "
													+ extractingFile
													+ ".Please retry "
													+ e.getMessage());
						}
						return null;
					}

				};
								worker.execute();
				/*Thread t = new Thread() {
					public void run() {

					}
				};
				t.start();*/
			}
		});

		smali2Class.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {

					@Override
					protected Void doInBackground() throws Exception {
						// TODO Auto-generated method stub
						String extractingFile = "Smali";
						try {
							ConsoleViewer.cleanConsole();
							JFileChooser chooser = new JFileChooser();
							chooser.setCurrentDirectory(new File(Utility
									.getProjectPath()));
							chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
							int choice = chooser.showOpenDialog(null);
							if (choice != JFileChooser.APPROVE_OPTION)
								return null;
							File chosenFile = chooser.getSelectedFile();
							File f = new File(chosenFile.getParentFile()
									.getAbsolutePath()
									+ File.separator
									+ chosenFile.getName() + "Converted.dex");
							File f2 = new File(chosenFile.getParentFile()
									.getAbsolutePath()
									+ File.separator
									+ chosenFile.getName() + "ConvertedCode");
							f2.mkdirs();
							ConsoleViewer.setText("First converting to dex");
							Utility.changeSmali2Dex(chosenFile, f);
							Utility.changeDex2Jar(f, f2);
							ConsoleViewer.setText(extractingFile
									+ " extracted at " + f2.getAbsolutePath());
							JOptionPane.showMessageDialog(null,
									"Verify console logs to confirm if process went smooth. "
											+ extractingFile + " extracted at "
											+ f2.getAbsolutePath());
						} catch (Exception e) {
							JOptionPane
									.showMessageDialog(
											null,
											"Issue while extracting "
													+ extractingFile
													+ ".Please retry "
													+ e.getMessage());
						}
						return null;
					}

				};
								worker.execute();
				/*Thread t = new Thread() {
					public void run() {

					}
				};
				t.start();*/
			}
		});

		smali2Java.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {

					@Override
					protected Void doInBackground() throws Exception {
						// TODO Auto-generated method stub
						String extractingFile = "Smali";
						try {
							ConsoleViewer.cleanConsole();
							JFileChooser chooser = new JFileChooser();
							chooser.setCurrentDirectory(new File(Utility
									.getProjectPath()));
							chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
							int choice = chooser.showOpenDialog(null);
							if (choice != JFileChooser.APPROVE_OPTION)
								return null;
							File chosenFile = chooser.getSelectedFile();
							File f = new File(chosenFile.getParentFile()
									.getAbsolutePath()
									+ File.separator
									+ chosenFile.getName() + "Converted.dex");
							File f2 = new File(chosenFile.getParentFile()
									.getAbsolutePath()
									+ File.separator
									+ chosenFile.getName() + "ConvertedCode");
							f2.mkdirs();
							ConsoleViewer.setText("First converting to dex");
							Utility.changeSmali2Dex(chosenFile, f);
							Utility.decompileJar2Java(f, f2.getAbsolutePath());
							ConsoleViewer.setText(extractingFile
									+ " extracted at " + f2.getAbsolutePath());
							JOptionPane.showMessageDialog(null,
									"Verify console logs to confirm if process went smooth. "
											+ extractingFile + " extracted at "
											+ f2.getAbsolutePath());
						} catch (Exception e) {
							JOptionPane
									.showMessageDialog(
											null,
											"Issue while extracting "
													+ extractingFile
													+ ".Please retry "
													+ e.getMessage());
						}
						return null;
					}

				};
								worker.execute();
/*				Thread t = new Thread() {
					public void run() {

					}
				};
				t.start();*/
			}
		});

		dex2Smali.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {

					@Override
					protected Void doInBackground() throws Exception {
						// TODO Auto-generated method stub
						String extractingFile = "Dex";
						try {
							ConsoleViewer.cleanConsole();
							FileNameExtensionFilter filter = new FileNameExtensionFilter(
									"Dex Files", "dex");
							JFileChooser chooser = new JFileChooser();
							chooser.setFileFilter(filter);
							chooser.setCurrentDirectory(new File(Utility
									.getProjectPath()));
							chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
							int choice = chooser.showOpenDialog(null);
							if (choice != JFileChooser.APPROVE_OPTION)
								return null;
							File chosenFile = chooser.getSelectedFile();
							if (!chosenFile.getName().contains(".dex")) {
								JOptionPane
										.showMessageDialog(null,
												"Invalid file type. Please provide dex file");
								throw new Exception("Invalid file type");
							}
							File f = new File(chosenFile.getParentFile()
									.getAbsolutePath()
									+ File.separator
									+ "extractedSmali");
							f.mkdirs();
							ConsoleViewer.setText("Converting the provided "
									+ extractingFile + "...");
							Utility.changeDex2Smali(chosenFile, f);
							ConsoleViewer.setText(extractingFile
									+ " extracted at " + f.getAbsolutePath());
							JOptionPane.showMessageDialog(null,
									"Verify console logs to confirm if process went smooth. "
											+ extractingFile + " extracted at "
											+ f.getAbsolutePath());
						} catch (Exception e) {
							JOptionPane
									.showMessageDialog(
											null,
											"Issue while extracting "
													+ extractingFile
													+ ".Please retry "
													+ e.getMessage());
						}
						return null;
					}

				};
								worker.execute();
				/*Thread t = new Thread() {
					public void run() {

					}
				};
				t.start();*/
			}
		});

		smali2Dex.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {

					@Override
					protected Void doInBackground() throws Exception {
						// TODO Auto-generated method stub
						String extractingFile = "Smali";
						try {
							ConsoleViewer.cleanConsole();
							JFileChooser chooser = new JFileChooser();
							chooser.setCurrentDirectory(new File(Utility
									.getProjectPath()));
							chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
							int choice = chooser.showOpenDialog(null);
							if (choice != JFileChooser.APPROVE_OPTION)
								return null;
							File chosenFile = chooser.getSelectedFile();
							File f = new File(chosenFile.getParentFile()
									.getAbsolutePath()
									+ File.separator
									+ chosenFile.getName() + "Converted.dex");
							ConsoleViewer.setText("Converting the provided "
									+ extractingFile + "...");
							Utility.changeSmali2Dex(chosenFile, f);
							ConsoleViewer.setText(extractingFile
									+ " extracted at " + f.getAbsolutePath());
							JOptionPane.showMessageDialog(null,
									"Verify console logs to confirm if process went smooth. "
											+ extractingFile + " extracted at "
											+ f.getAbsolutePath());
						} catch (Exception e) {
							JOptionPane
									.showMessageDialog(
											null,
											"Issue while extracting "
													+ extractingFile
													+ ".Please retry "
													+ e.getMessage());
						}
						return null;
					}

				};
								worker.execute();
				/*Thread t = new Thread() {
					public void run() {

					}
				};
				t.start();*/
			}
		});

		dexjar2Java.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {

					@Override
					protected Void doInBackground() throws Exception {
						// TODO Auto-generated method stub
						String extractingFile = "Dex/Jar";
						try {
							ConsoleViewer.cleanConsole();
							FileNameExtensionFilter filter = new FileNameExtensionFilter(
									"Dex/Jar Files", "dex", "jar");
							JFileChooser chooser = new JFileChooser();
							chooser.setFileFilter(filter);
							chooser.setCurrentDirectory(new File(Utility
									.getProjectPath()));
							chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
							int choice = chooser.showOpenDialog(null);
							if (choice != JFileChooser.APPROVE_OPTION)
								return null;
							File chosenFile = chooser.getSelectedFile();
							if (!chosenFile.getName().contains(".dex")
									&& !chosenFile.getName().contains(".jar")) {
								JOptionPane
										.showMessageDialog(null,
												"Invalid file type. Please provide dex/jar file");
								throw new Exception("Invalid file type");
							}
							File f = new File(chosenFile.getParentFile()
									.getAbsolutePath()
									+ File.separator
									+ "javaCode" + chosenFile.getName());
							f.mkdirs();
							ConsoleViewer.setText("Converting the provided "
									+ extractingFile + "...");
							Utility.decompileJar2Java(chosenFile,
									f.getAbsolutePath());
							ConsoleViewer.setText(extractingFile
									+ " extracted at " + f.getAbsolutePath());
							JOptionPane.showMessageDialog(null,
									"Verify console logs to confirm if process went smooth. "
											+ extractingFile + " extracted at "
											+ f.getAbsolutePath());
						} catch (Exception e) {
							JOptionPane
									.showMessageDialog(
											null,
											"Issue while extracting "
													+ extractingFile
													+ ".Please retry "
													+ e.getMessage());
						}
						return null;
					}

				};
								worker.execute();
				/*Thread t = new Thread() {
					public void run() {

					}
				};
				t.start();*/
			}
		});

		signAPK.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {

					@Override
					protected Void doInBackground() throws Exception {
						// TODO Auto-generated method stub
						String extractingFile = "APK";
						try {
							ConsoleViewer.cleanConsole();
							FileNameExtensionFilter filter = new FileNameExtensionFilter(
									"APK Files", "apk");
							JFileChooser chooser = new JFileChooser();
							chooser.setFileFilter(filter);
							chooser.setCurrentDirectory(new File(Utility
									.getProjectPath()));
							chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
							int choice = chooser.showOpenDialog(null);
							if (choice != JFileChooser.APPROVE_OPTION)
								return null;
							File chosenFile = chooser.getSelectedFile();
							if (!chosenFile.getName().contains(".apk")) {
								JOptionPane
										.showMessageDialog(null,
												"Invalid file type. Please provide apk file");
								throw new Exception("Invalid file type");
							}
							String output = chosenFile.getParentFile()
									.getAbsolutePath()
									+ File.separator
									+ "newSigned.apk";
							ConsoleViewer.setText("Converting the provided "
									+ extractingFile + "...");
							ConsoleViewer.setText("Signing the new apk");
							Utility.signApk(chosenFile.getAbsolutePath(),output);
							ConsoleViewer.setText("APK signed at " + output);
							JOptionPane.showMessageDialog(null,
									"APK signed at " + output);
						} catch (Exception e) {
							JOptionPane
									.showMessageDialog(
											null,
											"Issue while signing "
													+ extractingFile
													+ ".Please retry "
													+ e.getMessage());
						}
						return null;
					}

				};
								worker.execute();
				/*Thread t = new Thread() {
					public void run() {

					}
				};
				t.start();*/
			}
		});

		generateSmali.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {

					@Override
					protected Void doInBackground() throws Exception {
						// TODO Auto-generated method stub
						try {
							ConsoleViewer.cleanConsole();
							int chosenIndex = tabPane.getSelectedIndex();
							if (!tabPane.getToolTipTextAt(chosenIndex)
									.contains(".java")) {
								JOptionPane.showMessageDialog(null,
										"Invalid file type.");
								return null;
							}
							RTextScrollPane htmlScroll = (RTextScrollPane) tabPane
									.getComponentAt(chosenIndex);
							RSyntaxTextArea htmlPane = (RSyntaxTextArea) htmlScroll
									.getViewport().getView();
							String filePathLocal = tabPane
									.getToolTipTextAt(chosenIndex);
							filePathLocal = filePathLocal
									.substring(filePathLocal.indexOf("class"));
							String classPath = Utility.getSmaliPath()
									+ File.separator + filePathLocal;
							Utility.writeFile(classPath, htmlPane.getText());
							String dexFileName = filePathLocal.substring(0,
									filePathLocal.indexOf(File.separator));
							Utility.compileFile(filePathLocal,
									Utility.getLibraryPath(),
									new File(Utility.getSmaliPath()
											+ File.separator + dexFileName));
							File sourceFile = new File(classPath);
							File checkClassExist = new File(classPath.replace(
									".java", ".class"));
							if (checkClassExist.exists()) {
								if (sourceFile.lastModified() < checkClassExist
										.lastModified()) {
									String fileName = checkClassExist.getName();
									String firstPart = fileName.substring(0,
											fileName.indexOf(".class"));
									String lastPart = ".class";
									List<String> foundedClass = Utility
											.searchInternalClass(firstPart,
													lastPart, checkClassExist
															.getParentFile());
									// foundedClass.add(checkClassExist.getName());
									for (String foundc : foundedClass) {
										String newName = foundc.substring(0,
												foundc.indexOf(".class"));
										Utility.changeClass2Dex(
												checkClassExist.getParent()
														+ File.separator
														+ foundc,
												Utility.getSmaliPath()
														+ File.separator
														+ "convertedDex.dex");
										Utility.changeDex2Smali(new File(
												Utility.getSmaliPath()
														+ File.separator
														+ "convertedDex.dex"),
												new File(Utility.getSmaliPath()
														+ File.separator
														+ dexFileName));
										RSyntaxTextArea textArea = new RSyntaxTextArea();
										textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
										textArea.setCodeFoldingEnabled(true);
										Font font = new Font(Font.SANS_SERIF,
												Font.PLAIN, 16);
										textArea.setFont(font);
										RTextScrollPane htmlView = new RTextScrollPane(
												textArea);
										File file = new File(Utility
												.getSmaliPath()
												+ File.separator
												+ filePathLocal.replace(
														".java", ".smali")
														.replace(firstPart,
																newName));
										textArea.setText(FileUtils
												.readFileToString(file, "UTF-8"));
										String tabName = file.getName();
										tabPane.addTab(tabName, htmlView);
										int tabIndex = tabPane.getTabCount() - 1;
										tabPane.setSelectedIndex(tabIndex);
										tabPane.setToolTipTextAt(
												tabIndex,
												filePathLocal.replace(".java",
														".smali").replace(
														firstPart, newName));
										openedTabs.put(
												filePathLocal.replace(".java",
														".smali").replace(
														firstPart, newName),
												tabIndex);
										textArea.setCaretPosition(0);
									}
								} else {
									JOptionPane
											.showMessageDialog(null,
													"Compilation failed. Cannot convert to smali");
								}
							} else {
								JOptionPane
										.showMessageDialog(null,
												"Compilation failed. Cannot convert to smali");
							}
						} catch (Exception e) {
							// e.printStackTrace();
							ConsoleViewer
									.setText("Issue while generating smali.Please retry "
											+ e.getMessage());
							JOptionPane.showMessageDialog(null,
									"Issue while generating smali.Please retry "
											+ e.getMessage());
						}
						return null;
					}

				};
								worker.execute();
				/*Thread t = new Thread() {
					public void run() {

					}
				};
				t.start();*/
			}
		});

		editSmali.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {

					@Override
					protected Void doInBackground() throws Exception {
						// TODO Auto-generated method stub
						try {
							ConsoleViewer.cleanConsole();
							int chosenIndex = tabPane.getSelectedIndex();
							if (!tabPane.getToolTipTextAt(chosenIndex)
									.contains(".java")) {
								JOptionPane.showMessageDialog(null,
										"Invalid file type.");
								return null;
							}
							String filePathLocal = tabPane
									.getToolTipTextAt(chosenIndex);
							filePathLocal = filePathLocal
									.substring(filePathLocal.indexOf("class"));
							String folderName = "";
							String classFolder = filePathLocal.substring(0,
									filePathLocal.indexOf(".dex")) + ".dex";
							if (filePathLocal.toLowerCase().contains(
									"classes.dex")) {
								folderName = "smali";
							} else {
								folderName = "smali_"
										+ filePathLocal.substring(0,
												filePathLocal.indexOf(".dex"));
							}
							String classPath = Utility.getApkToolSource()
									+ File.separator
									+ folderName
									+ File.separator
									+ filePathLocal.substring(filePathLocal
											.indexOf(".dex") + 4);
							classPath = classPath.replace(".java", ".smali");
							File smaliDir = new File(classPath);
							String fileName = smaliDir.getName();
							String firstPart = fileName.substring(0,
									fileName.indexOf(".smali"));
							String lastPart = ".smali";
							List<File> foundedSmali = Utility
									.searchInternalSmali(firstPart, lastPart,
											smaliDir.getParentFile());
							// foundedSmali.add(smaliDir);
							for (File sm : foundedSmali) {
								RSyntaxTextArea textArea = new RSyntaxTextArea();
								textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
								textArea.setCodeFoldingEnabled(true);
								Font font = new Font(Font.SANS_SERIF,
										Font.PLAIN, 16);
								textArea.setFont(font);
								RTextScrollPane htmlView = new RTextScrollPane(
										textArea);
								File destSmali = new File(Utility
										.getSmaliPath()
										+ File.separator
										+ classFolder
										+ File.separator
										+ filePathLocal.substring(filePathLocal
												.indexOf(".dex") + 4));
								destSmali = destSmali.getParentFile();
								File finalDestSmaliPath = new File(destSmali
										.getAbsolutePath()
										+ File.separator
										+ sm.getName());
								FileUtils.copyFile(sm, finalDestSmaliPath);
								textArea.setText(FileUtils.readFileToString(sm,
										"UTF-8"));
								String tabName = sm.getName();
								tabPane.addTab(tabName, htmlView);
								int tabIndex = tabPane.getTabCount() - 1;
								tabPane.setSelectedIndex(tabIndex);
								tabPane.setToolTipTextAt(
										tabIndex,
										filePathLocal
												.replace(".java", ".smali")
												.replace(fileName, sm.getName()));
								openedTabs.put(
										filePathLocal
												.replace(".java", ".smali")
												.replace(fileName, sm.getName()),
										tabIndex);
								textArea.setCaretPosition(0);
							}
						} catch (Exception e) {
							// e.printStackTrace();
							ConsoleViewer
									.setText("Issue while saving smali changes.Please retry "
											+ e.getMessage());
							JOptionPane.showMessageDialog(null,
									"Issue while saving smali changes.Please retry "
											+ e.getMessage());
						}
						return null;
					}

				};
								worker.execute();
				/*Thread t = new Thread() {
					public void run() {

					}
				};
				t.start();*/
			}
		});

		saveSmaliChange.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {

					@Override
					protected Void doInBackground() throws Exception {
						// TODO Auto-generated method stub
						try {
							ConsoleViewer.cleanConsole();
							int chosenIndex = tabPane.getSelectedIndex();
							if (!tabPane.getToolTipTextAt(chosenIndex)
									.contains(".smali")) {
								JOptionPane.showMessageDialog(null,
										"Invalid file type.");
								return null;
							}
							RTextScrollPane htmlScroll = (RTextScrollPane) tabPane
									.getComponentAt(chosenIndex);
							RSyntaxTextArea htmlPane = (RSyntaxTextArea) htmlScroll
									.getViewport().getView();
							String filePathLocal = tabPane
									.getToolTipTextAt(chosenIndex);
							String folderName = filePathLocal.substring(0,
									filePathLocal.indexOf(File.separator));
							String packageName = filePathLocal
									.substring(filePathLocal.indexOf(".dex") + 4);
							File smaliPath = new File(Utility.getSmaliPath()
									+ File.separator + folderName
									+ File.separator + packageName);
							File dexPath = new File(Utility.getSmaliPath()
									+ File.separator + "convertedDex.dex");
							File outputFile = new File(Utility.getClassFile()
									+ File.separator + folderName);
							File javaFolder = new File(Utility
									.getJavaCodeFolder()
									+ File.separator
									+ folderName);
							FileUtils.write(smaliPath, htmlPane.getText(),
									"UTF-8");
							String fileName = smaliPath.getName();
							String firstPart = fileName.substring(0,
									fileName.indexOf(".smali"));
							String lastPart = ".smali";
							List<File> foundedSmali = Utility
									.searchInternalSmali(firstPart, lastPart,
											smaliPath.getParentFile());
							// foundedSmali.add(smaliPath);
							File f = new File(Utility.getSmaliPath()
									+ File.separator + "smaliConvert");
							f.mkdirs();
							for (File f1 : foundedSmali) {
								FileUtils.copyFile(
										f1,
										new File(f.getAbsolutePath()
												+ File.separator + f1.getName()));
							}
							Utility.changeSmali2Dex(f, dexPath);
							FileUtils.deleteDirectory(f);
							Utility.changeDex2Jar(dexPath, outputFile);
							Utility.decompileJar2Java(dexPath,
									javaFolder.getAbsolutePath());
							String newPath = packageName.replace(".smali",
									".java");
							if (newPath.contains("$")) {
								newPath = packageName.substring(0,
										packageName.indexOf("$"))
										+ ".java";
							}
							String javaFile = javaFolder.getAbsolutePath()
									+ File.separator + newPath;
							newPath = filePathLocal.replace(".smali", ".java");
							if (newPath.contains("$")) {
								newPath = filePathLocal.substring(0,
										filePathLocal.indexOf("$")) + ".java";
							}
							String javaFilePath = File.separator
									+ File.separator + newPath;
							for (int i = 0; i < tabPane.getTabCount(); i++) {
								if (tabPane.getToolTipTextAt(i).equals(
										javaFilePath)
										|| tabPane.getToolTipTextAt(i).equals(
												javaFilePath)) {
									String title = tabPane.getTitleAt(i);
									if (title.startsWith("*")) {
										title = title.replaceFirst("\\*", "");
									}
									tabPane.setTitleAt(i, title);
									RTextScrollPane htmlScroll2 = (RTextScrollPane) tabPane
											.getComponentAt(i);
									RSyntaxTextArea htmlPane2 = (RSyntaxTextArea) htmlScroll2
											.getViewport().getView();
									htmlPane2.setText(FileUtils
											.readFileToString(
													new File(javaFile), "UTF-8"));
									break;
								}
							}
							JOptionPane.showMessageDialog(null,
									"Updated and saved the java code");
						} catch (Exception e) {
							// e.printStackTrace();
							ConsoleViewer
									.setText("Issue while saving smali changes.Please retry "
											+ e.getMessage());
							JOptionPane.showMessageDialog(null,
									"Issue while saving smali changes.Please retry "
											+ e.getMessage());
						}
						return null;
					}

				};
								worker.execute();
				/*Thread t = new Thread() {
					public void run() {

					}
				};
				t.start();*/
			}
		});

		incFont.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				try {
					int chosenIndex = tabPane.getSelectedIndex();
					RTextScrollPane htmlScroll = (RTextScrollPane) tabPane
							.getComponentAt(chosenIndex);
					RSyntaxTextArea htmlPane = (RSyntaxTextArea) htmlScroll
							.getViewport().getView();
					Font font = new Font(Font.SANS_SERIF, Font.PLAIN, htmlPane
							.getFont().getSize() + 1);
					htmlPane.setFont(font);
				} catch (Exception e) {

				}
			}
		});

		decFont.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				try {
					int chosenIndex = tabPane.getSelectedIndex();
					RTextScrollPane htmlScroll = (RTextScrollPane) tabPane
							.getComponentAt(chosenIndex);
					RSyntaxTextArea htmlPane = (RSyntaxTextArea) htmlScroll
							.getViewport().getView();
					Font font = new Font(Font.SANS_SERIF, Font.PLAIN, htmlPane
							.getFont().getSize() - 1);
					htmlPane.setFont(font);
				} catch (Exception e) {

				}
			}
		});

		removeAllTab.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				try {
					tabPane.removeAll();
					openedTabs.clear();
				} catch (Exception e) {

				}
			}
		});

		save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {

					@Override
					protected Void doInBackground() throws Exception {
						// TODO Auto-generated method stub
						try {
							ConsoleViewer.cleanConsole();
							int chosenIndex = tabPane.getSelectedIndex();
							if (tabPane.getToolTipTextAt(chosenIndex).contains(
									".smali")) {
								JOptionPane.showMessageDialog(null,
										"Use Advanced --> Save Smali changes");
								return null;
							}
							RTextScrollPane htmlScroll = (RTextScrollPane) tabPane
									.getComponentAt(chosenIndex);
							RSyntaxTextArea htmlPane = (RSyntaxTextArea) htmlScroll
									.getViewport().getView();
							String filePathLocal = tabPane
									.getToolTipTextAt(chosenIndex);
							filePathLocal = filePathLocal
									.substring(filePathLocal.indexOf("class"));
							String classPath = Utility.getClassFile()
									+ File.separator + filePathLocal;
							Utility.writeFile(classPath, htmlPane.getText());
							FileUtils.copyFile(new File(classPath), new File(
									Utility.getJavaCodeFolder()
											+ File.separator + filePathLocal));
							String dexFileName = filePathLocal.substring(0,
									filePathLocal.indexOf(File.separator));
							Utility.compileFile(filePathLocal,
									Utility.getLibraryPath(),
									new File(Utility.getClassFile()
											+ File.separator + dexFileName));
							File sourceFile = new File(classPath);
							File checkClassExist = new File(classPath.replace(
									".java", ".class"));
							if (checkClassExist.exists()) {
								if (sourceFile.lastModified() < checkClassExist
										.lastModified()) {
									String title = tabPane
											.getTitleAt(chosenIndex);
									if (!title.startsWith("\\*")) {
										title = title.replaceFirst("\\*", "");
									}
									tabPane.setTitleAt(chosenIndex, title);
									JOptionPane.showMessageDialog(null,
											"Compiled Successfully");
								} else {
									JOptionPane.showMessageDialog(null,
											"Compilation failed");
								}
							} else {
								JOptionPane.showMessageDialog(null,
										"Compilation failed");
							}
						} catch (Exception e) {
							e.printStackTrace();
							JOptionPane.showMessageDialog(
									null,
									"Issue while compiling.Please retry "
											+ e.getMessage());
						}
						return null;
					}

				};
								worker.execute();
				/*Thread t = new Thread() {
					public void run() {

					}
				};
				t.start();*/
			}
		});
		openProj.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {

					@Override
					protected Void doInBackground() throws Exception {
						// TODO Auto-generated method stub
						try {
							ConsoleViewer.cleanConsole();
							JFileChooser chooser = new JFileChooser();
							chooser.setCurrentDirectory(new File(Utility
									.getProjectPath()));
							chooser.setDialogTitle("Open APKRepatcher Project");
							chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
							int choice = chooser.showOpenDialog(null);
							if (choice != JFileChooser.APPROVE_OPTION)
								return null;
							File chosenFile = chooser.getSelectedFile();
							Utility.apkPath = new File(chosenFile
									.getAbsolutePath()
									+ File.separator
									+ chosenFile.getName());
							Utility.apkName = chosenFile.getName();
							Utility.searchAndCopyDexFile(
									Utility.getApkSource(),
									Utility.getDexPath());
							tabPane.removeAll();
							openedTabs.clear();
							fileRoot = new File(Utility.getJavaCodeFolder());
							root = new DefaultMutableTreeNode();
							treeModel = new DefaultTreeModel(root);
							tree = new JTree(treeModel);
							Font font = new Font(Font.SANS_SERIF, Font.PLAIN,
									16);
							tree.setFont(font);
							tree.getSelectionModel().setSelectionMode(
									TreeSelectionModel.SINGLE_TREE_SELECTION);
							tree.setShowsRootHandles(true);
							tree.addMouseListener(new MouseAdapter() {
								public void mouseClicked(MouseEvent me) {
									doMouseClicked(me);
								}
							});
							CreateChildNodes ccn = new CreateChildNodes(
									fileRoot, root);
							new Thread(ccn).start();
							splitPane.setDividerLocation(0.15);
							JScrollPane treeView = new JScrollPane(tree);
							splitPane.setLeftComponent(treeView);
							treeView.repaint();
							splitPane.repaint();
							splitPane.setDividerLocation(0.15);
							frame.setTitle(title + " - " + Utility.apkName);
						} catch (Exception e) {
							JOptionPane.showMessageDialog(
									null,
									"Issue while opening project "
											+ e.getMessage());
						}
						return null;
					}

				};
								worker.execute();
				/*Thread t = new Thread() {
					public void run() {

					}
				};
				t.start();*/
			}
		});
		open.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {

					@Override
					protected Void doInBackground() throws Exception {
						// TODO Auto-generated method stub
						try {
							ConsoleViewer.cleanConsole();
							FileNameExtensionFilter filter = new FileNameExtensionFilter(
									"APK Files", "apk");
							JFileChooser chooser = new JFileChooser();
							chooser.setFileFilter(filter);
							chooser.setDialogTitle("Open APK File");
							chooser.setCurrentDirectory(new File(Utility
									.getProjectPath()));
							chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
							int choice = chooser.showOpenDialog(null);
							if (choice != JFileChooser.APPROVE_OPTION)
								return null;
							File chosenFile = chooser.getSelectedFile();
							if (!chosenFile.getName().contains(".apk")) {
								JOptionPane
										.showMessageDialog(null,
												"Invalid file type. Please provide apk file");
							}
							tabPane.removeAll();
							openedTabs.clear();
							Utility.apkPath = chosenFile;
							Utility.apkName = chosenFile.getName();
							PrepareProject.start();
							Utility.sleep(1);
							FileUtils.copyFile(
									chosenFile,
									new File(Utility.getProjectPath()
											+ File.separator
											+ chosenFile.getName()));
							fileRoot = new File(Utility.getJavaCodeFolder());
							root = new DefaultMutableTreeNode();
							treeModel = new DefaultTreeModel(root);
							tree = new JTree(treeModel);
							Font font = new Font(Font.SANS_SERIF, Font.PLAIN,
									16);
							tree.setFont(font);
							tree.getSelectionModel().setSelectionMode(
									TreeSelectionModel.SINGLE_TREE_SELECTION);
							tree.setShowsRootHandles(true);
							tree.addMouseListener(new MouseAdapter() {
								public void mouseClicked(MouseEvent me) {
									doMouseClicked(me);
								}
							});
							CreateChildNodes ccn = new CreateChildNodes(
									fileRoot, root);
							new Thread(ccn).start();
							JScrollPane treeView = new JScrollPane(tree);
							splitPane.setDividerLocation(0.15);
							splitPane.setLeftComponent(treeView);
							treeView.repaint();
							splitPane.repaint();
							splitPane.setDividerLocation(0.15);
							JOptionPane.showMessageDialog(null,
									"Project Created");
							frame.setTitle(title + " - " + Utility.apkName);
						} catch (Exception e) {
							e.printStackTrace();
							JOptionPane.showMessageDialog(null,
									"Something went wrong...");
						}
						return null;
					}

				};
								worker.execute();
				/*Thread t = new Thread() {
					public void run() {

					}
				};
				t.start();*/
			}
		});
		build.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {

					@Override
					protected Void doInBackground() throws Exception {
						// TODO Auto-generated method stub
						try {
							ConsoleViewer.cleanConsole();
							for (File dexFile : Utility.dexFileList) {
								File outputJarPath = new File(Utility
										.getDex2JarPath()
										+ File.separator
										+ dexFile.getName().replace(".dex",
												".jar"));
								File modifiedJarPath = new File(Utility
										.getModifiedJar()
										+ File.separator
										+ dexFile.getName().replace(".dex",
												".jar"));
								FileUtils.copyFile(outputJarPath,
										modifiedJarPath);
							}
							// update the jar with modified class
							Utility.repackageJar();
							FileUtils.copyFile(Utility.apkPath, new File(
									Utility.getProjectPath() + File.separator
											+ "newApk.apk"));
							ConsoleViewer
									.setText("Converting the new jar into dex");
							Utility.changeJar2Dex(Utility.getModifiedJar());
							ConsoleViewer
									.setText("Converted the new jar into dex");
							ConsoleViewer.setText("Rewriting the dex into apk");
							Utility.rewriteDexInAPK(Utility.getProjectPath()
									+ File.separator + "newApk.apk");
							ConsoleViewer.setText("New dex written");
							ConsoleViewer.setText("Signing the new apk");
							Utility.signApk(Utility.getProjectPath()
									+ File.separator + "newApk.apk",
									Utility.getProjectPath() + File.separator
											+ "newSigned.apk");
							ConsoleViewer.setText("Signed the new apk");
							if (!Utility.certificateName.equals("CERT")) {
								ConsoleViewer
										.setText("Renaming the Certificate");
								Utility.repackCert(Utility.getProjectPath()
										+ File.separator + "newSigned.apk");
								ConsoleViewer
										.setText("Renamed the Certificate");
							}
							new File(Utility.getProjectPath() + File.separator+ "newApk.apk").delete();
							ConsoleViewer
							.setText("Build Successful. New APK - "
									+ Utility.getProjectPath()
									+ File.separator + "newSigned.apk");
							JOptionPane.showMessageDialog(
									null,
									"Build Successful. New APK - "
											+ Utility.getProjectPath()
											+ File.separator + "newSigned.apk");
						} catch (Exception e) {
							e.printStackTrace();
							JOptionPane.showMessageDialog(
									null,
									"Issue while building project "
											+ e.getMessage());
						}
						return null;
					}

				};
								worker.execute();
				/*Thread t = new Thread() {
					public void run() {

					}
				};
				t.start();*/
			}
		});
		tabPane = new ClosableTabbedPane() {
			public boolean tabAboutToClose(int tabIndex) {
				// int currSel=currentTabIndex;
				if (tabIndex == previousTabIndex) {
					openedTabs.remove(tabPane.getToolTipTextAt(tabIndex));
				} else {
					openedTabs.remove(tabPane.getToolTipTextAt(tabIndex));
					try {
						tabPane.setSelectedIndex(previousTabIndex);
					} catch (Exception e) {
					}
				}

				return true;
			}
		};
		tabPane.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent ce) {
				previousTabIndex = currentTabIndex;
				currentTabIndex = tabPane.getSelectedIndex();
			}
		});
		Font font = new Font(Font.SANS_SERIF, Font.PLAIN, 14);
		tabPane.setFont(font);
		tabPane.setTabLayoutPolicy(JTabbedPane.WRAP_TAB_LAYOUT);
		// htmlPane.setBackground(Color.WHITE);
		// htmlPane.setForeground(Color.BLACK);
		consoleArea = new JTextArea("Console...\n");
		consoleArea.setBackground(Color.BLACK);
		consoleArea.setForeground(Color.WHITE);
		DefaultCaret caret = (DefaultCaret) consoleArea.getCaret();
		caret.setUpdatePolicy(DefaultCaret.OUT_BOTTOM);
		// Font font = new Font(Font.SANS_SERIF, Font.PLAIN, 20);
		Font font2 = new Font(Font.SANS_SERIF, Font.ITALIC, 16);
		// htmlPane.setFont(font);
		consoleArea.setFont(font2);
		// JScrollPane htmlView = new JScrollPane(htmlPane);
		JScrollPane consoleView = new JScrollPane(consoleArea);
		consoleView.scrollRectToVisible(new Rectangle(0, consoleArea
				.getBounds(null).height, 1, 1));
		splitPaneInternal = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		// splitPaneInternal.setTopComponent(htmlView);
		splitPaneInternal.setTopComponent(tabPane);
		splitPaneInternal.setBottomComponent(consoleView);
		splitPaneInternal.setResizeWeight(0.85);
		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		splitPane.setDividerLocation(250);
		splitPane.setLeftComponent(new JLabel());
		splitPane.setRightComponent(splitPaneInternal);
		frame.setJMenuBar(menuBar);

		JToolBar toolBar = new JToolBar();
		JLabel fi = new JLabel("Find (ALT+F) ");
		toolBar.add(fi);
		searchField = new JTextField(15);
		fi.setLabelFor(searchField);
		fi.setDisplayedMnemonic(KeyEvent.VK_F);
		toolBar.add(searchField);
		JLabel rep = new JLabel("Replace with (ALT+R) ");
		toolBar.add(rep);
		repWithField = new JTextField(15);
		rep.setLabelFor(repWithField);
		rep.setDisplayedMnemonic(KeyEvent.VK_R);
		toolBar.add(repWithField);
		final JButton findButton = new JButton("Find Text");
		findButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				searchText("find", regexCB.isSelected(),
						matchCaseCB.isSelected(), wholeWord.isSelected(),
						markAll.isSelected(), forSearch.isSelected());
			}
		});
		toolBar.add(findButton);
		final JButton findClass = new JButton("Find Class");
		findClass.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				searchClass(searchField.getText());
			}
		});
		toolBar.add(findClass);
		searchField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				findButton.doClick(0);
			}
		});
		JButton repWithButton = new JButton("Replace With");
		repWithButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				searchText("replace", regexCB.isSelected(),
						matchCaseCB.isSelected(), wholeWord.isSelected(),
						markAll.isSelected(), forSearch.isSelected());
			}
		});
		toolBar.add(repWithButton);
		JButton repAll = new JButton("Replace All");
		repAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				searchText("replaceAll", regexCB.isSelected(),
						matchCaseCB.isSelected(), wholeWord.isSelected(),
						markAll.isSelected(), forSearch.isSelected());
			}
		});
		toolBar.add(repAll);
		forSearch = new JCheckBox("Forward Search");
		forSearch.setSelected(true);
		toolBar.add(forSearch);
		backSearch = new JCheckBox("Backward Search");
		toolBar.add(backSearch);
		regexCB = new JCheckBox("Regex");
		toolBar.add(regexCB);
		matchCaseCB = new JCheckBox("Match Case");
		toolBar.add(matchCaseCB);
		wholeWord = new JCheckBox("Whole Word");
		toolBar.add(wholeWord);
		markAll = new JCheckBox("MarkAll");
		markAll.setSelected(true);
		//toolBar.add(markAll);
		Utility.retrieveAllocatedMemory();
		memLabel=new JLabel("Memory Allocated: "+Utility.memoryAllocated+Utility.memUnit);
		toolBar.add(memLabel);
		updateAvail = new JButton();
		updateAvail.setHorizontalAlignment(SwingConstants.LEFT);
		updateAvail.setBorderPainted(false);
		updateAvail.setOpaque(false);
		updateAvail.setBackground(Color.WHITE);
		updateAvail.setForeground(Color.BLUE);
		updateAvail.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateSoft.doClick(0);
			}
		});
		toolBar.add(updateAvail);
		try {
			versionChecker = new JEditorPane();
			versionChecker
					.addPropertyChangeListener(new PropertyChangeListener() {
						public void propertyChange(PropertyChangeEvent evt) {
							if (!evt.getPropertyName().equals("page")) {
								return;
							}
							String newVersion = versionChecker.getText()
									.replaceAll("\n", "");
							if (!(newVersion).equals(currVersion)) {
								updateAvail
										.setText("<html><u>Update APKRepatcher to version "
												+ newVersion + "</u></html>");
							}
						}
					});
			SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {

				@Override
				protected Void doInBackground() throws Exception {
					// TODO Auto-generated method stub
					try {
						versionChecker.setPage(checkVersion);
					} catch (Exception e) {
					}
					return null;
				}

			};
							worker.execute();
			/*new Thread() {
				public void run() {
					
				}
			}.start();*/
		} catch (Exception e) {
		}
		JSplitPane splitPaneOuter = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		toolBar.setLayout(new FlowLayout(0));
		// splitPaneInternal.setTopComponent(htmlView);
		splitPaneOuter.setTopComponent(toolBar);
		splitPaneOuter.setBottomComponent(splitPane);
		splitPaneOuter.setResizeWeight(0.0001);
		frame.add(splitPaneOuter);
		// frame.add(splitPane);
		frame.setExtendedState(frame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
		frame.setLocationByPlatform(true);
		frame.setVisible(true);
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
		    @Override
		    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
		    	Utility.destroyDanglingProcess();
		    }
		});
	}

	public void searchClass(String className) {
		final String searchClass = className;
		SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {

			@Override
			protected Void doInBackground() throws Exception {
				// TODO Auto-generated method stub
				try {
					ConsoleViewer.cleanConsole();
					@SuppressWarnings("unchecked")
					Enumeration<DefaultMutableTreeNode> e = root
							.depthFirstEnumeration();
					while (e.hasMoreElements()) {
						DefaultMutableTreeNode node = e.nextElement();
						if (node.toString().toLowerCase()
								.contains(searchClass.toLowerCase())) {
							TreePath path = new TreePath(node.getPath());
							String pathFinal = "";
							for (Object o : path.getPath()) {
								pathFinal += (o + File.separator);
							}
							ConsoleViewer.setText(pathFinal);
							tree.setSelectionPath(path);
							tree.scrollPathToVisible(path);
							// return new TreePath(node.getPath());
						}
					}
					JOptionPane.showMessageDialog(null,
							"Search complete, result shown in console.");
				} catch (Exception e) {
					JOptionPane.showMessageDialog(null,
							"Issue while searching class " + e.getMessage());
				}
				return null;
			}

		};
						worker.execute();
	/*	Thread t = new Thread() {
			public void run() {

			}
		};
		t.start();*/
	}

	public void searchText(String operation, boolean regex, boolean matchCase,
			boolean wholeWord, boolean markAll, boolean forSearch) {
		int chosenIndex = tabPane.getSelectedIndex();
		if (chosenIndex < 0) {
			JOptionPane.showMessageDialog(null, "No files opened to search on");
			return;
		}
		RTextScrollPane htmlScroll = (RTextScrollPane) tabPane
				.getComponentAt(chosenIndex);
		RSyntaxTextArea searchtextArea = (RSyntaxTextArea) htmlScroll
				.getViewport().getView();
		SearchContext context = new SearchContext();
		String text = searchField.getText();
		if (text.length() == 0) {
			return;
		}
		context.setSearchFor(text);
		context.setMatchCase(matchCase);
		context.setRegularExpression(regex);
		context.setSearchForward(forSearch);
		context.setWholeWord(wholeWord);
		context.setMarkAll(markAll);
		boolean found = false;
		if (operation.equals("replace")) {
			context.setReplaceWith(repWithField.getText());
			found = SearchEngine.replace(searchtextArea, context).wasFound();
		} else if (operation.equals("replaceAll")) {
			context.setReplaceWith(repWithField.getText());
			found = SearchEngine.replaceAll(searchtextArea, context).wasFound();
		} else {
			found = SearchEngine.find(searchtextArea, context).wasFound();
		}
		if (!found) {
			JOptionPane.showMessageDialog(null, "Text not found");
		}
	}

	public static RSyntaxTextArea getHtmlPane() {
		/*
		 * JEditorPane htmlPane = new JEditorPane();
		 * htmlPane.setBackground(Color.WHITE);
		 * htmlPane.setForeground(Color.BLACK); Font font = new
		 * Font(Font.SANS_SERIF, Font.PLAIN, 20); htmlPane.setFont(font);
		 */
		RSyntaxTextArea textArea = new RSyntaxTextArea();
		textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
		textArea.setCodeFoldingEnabled(true);
		Font font = new Font(Font.SANS_SERIF, Font.PLAIN, 16);
		textArea.setFont(font);
		return textArea;
	}

	public static String createFilePath(TreePath treePath) {
		StringBuilder sb = new StringBuilder();
		Object[] nodes = treePath.getPath();
		for (int i = 0; i < nodes.length; i++) {
			sb.append(File.separatorChar).append(nodes[i].toString());
		}
		return sb.toString();
	}

	void doMouseClicked(MouseEvent me) {
		TreePath tp = tree.getPathForLocation(me.getX(), me.getY());
		if (tp != null) {
			try {
				filePath = createFilePath(tp);
				if (filePath.endsWith("java")) {
					if (!openedTabs.containsKey(filePath)) {
						if (tabPane.getTabCount() < 200) {
							RSyntaxTextArea htmlPane = getHtmlPane();
							RTextScrollPane htmlView = new RTextScrollPane(
									htmlPane);
							File file = new File(Utility.getJavaCodeFolder()
									+ File.separator + filePath);
							htmlPane.setText(FileUtils.readFileToString(file,
									"UTF-8"));
							String tabName = filePath.substring(
									filePath.lastIndexOf(File.separator) + 1,
									filePath.length());
							// tabPane.setBackground(Color.RED);
							tabPane.addTab(tabName, htmlView);
							int tabIndex = tabPane.getTabCount() - 1;
							tabPane.setSelectedIndex(tabIndex);
							tabPane.setToolTipTextAt(tabIndex, filePath);
							openedTabs.put(filePath, tabIndex);
							// currentTabIndex=tabIndex;
							htmlPane.setCaretPosition(0);
							htmlPane.getDocument().addDocumentListener(
									new DocumentListener() {

										@Override
										public void removeUpdate(DocumentEvent e) {
											int ind = tabPane
													.getSelectedIndex();
											String title = tabPane
													.getTitleAt(ind);
											if (title.contains(".smali")) {
												return;
											}
											if (!title.startsWith("*")) {
												title = "*" + title;
											}
											tabPane.setTitleAt(ind, title);
										}

										@Override
										public void insertUpdate(DocumentEvent e) {
											int ind = tabPane
													.getSelectedIndex();
											String title = tabPane
													.getTitleAt(ind);
											if (title.contains(".smali")) {
												return;
											}
											if (!title.startsWith("*")) {
												title = "*" + title;
											}
											tabPane.setTitleAt(ind, title);
										}

										@Override
										public void changedUpdate(
												DocumentEvent arg0) {

										}
									});
						} else {
							JOptionPane
									.showMessageDialog(null,
											"Excedded maximum limit of 200 tabs. Please close some tabs");
						}
					} else {
						for (int i = 0; i < tabPane.getTabCount(); i++) {
							if (tabPane.getToolTipTextAt(i).equals(filePath)
									|| tabPane.getToolTipTextAt(i).equals(
											"*" + filePath)) {
								tabPane.setSelectedIndex(i);
								break;
							}
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(null,
						"File Not Found " + e.getMessage());
			}
		}
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new APKRepatcher());
	}

	public class CreateChildNodes implements Runnable {

		private DefaultMutableTreeNode root;

		private File fileRoot;

		public CreateChildNodes(File fileRoot, DefaultMutableTreeNode root) {
			this.fileRoot = fileRoot;
			this.root = root;
		}

		@Override
		public void run() {
			createChildren(fileRoot, root);
			DefaultMutableTreeNode currentNode = root.getNextNode();
			if (currentNode.getLevel() == 1)
				tree.expandPath(new TreePath(currentNode.getPath()));
		}

		private void createChildren(File fileRoot, DefaultMutableTreeNode node) {
			File[] files = fileRoot.listFiles();
			if (files == null)
				return;

			for (File file : files) {
				DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(
						new FileNode(file));
				node.add(childNode);
				if (file.isDirectory()) {
					createChildren(file, childNode);
				}
			}
		}

	}

	public class FileNode {

		private File file;

		public FileNode(File file) {
			this.file = file;
		}

		@Override
		public String toString() {
			String name = file.getName();
			if (name.equals("")) {
				return file.getAbsolutePath();
			} else {
				return name;
			}
		}
	}

}
