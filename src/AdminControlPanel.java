import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.*;
import java.util.Arrays;
import java.util.List;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

/**
 * main UI for application
 */
public class AdminControlPanel extends JPanel {

    public static List<View> allRootViews = new ArrayList<>();
    private static AdminControlPanel instance;

    private AdminControlPanel() {

        UserCounter userCounter = new UserCounter();
        GroupCounter groupCounter = new GroupCounter();
        LastUpdated lastUpdated = new LastUpdated();

        VisitorCollection visitorCollection = new VisitorCollection();
        visitorCollection.setCollection(Arrays.asList(new TwitterElement[]{userCounter, groupCounter,lastUpdated}));

        UserCountVisitor userCountVisitor = new UserCountVisitor();
        GroupCountVisitor groupCountVisitor = new GroupCountVisitor();
        LastUpdatedVisitor lastUpdatedVisitor = new LastUpdatedVisitor();

        //Demonstrate Composite pattern with Frame, Panels and Buttons
        TwitterJFrame tJFrame = new TwitterJFrame();

        //all elements nested inside
        JPanel outerMostPanel = new JPanel(new BorderLayout());

        //JTree nested inside only, nested directly in outerMostPanel
        TwitterJPanel treeViewPanel = new TwitterJPanel(new BorderLayout());

        //JTextField and JButton for adding users nested inside, nested directly in addPanel
        TwitterJPanel addUserPanel = new TwitterJPanel(new BorderLayout());

        //JTextField and JButton for adding groups nested inside, nested directly in addPanel
        TwitterJPanel addGroupPanel = new TwitterJPanel(new BorderLayout());

        //addUserPanel and addGroupPanel nested directly inside, nested directly in outerEastPanel
        TwitterJPanel addPanel = new TwitterJPanel(new BorderLayout());

        //JButton openUserView nested directly inside, nested directly in outerEastPanel
        JPanel openUserViewPanel = new JPanel(new BorderLayout());
        openUserViewPanel.setBorder(BorderFactory.createEmptyBorder(150, 5, 150, 5));

        //addPanel, openUserViewPanel, showPanel nested directly inside, nested directly in outerMostPanel
        TwitterJPanel outerEastPanel = new TwitterJPanel(new BorderLayout());

        //JButtons showUserTotal, showGroupTotal nested directly inside, inside showPanel
        TwitterJPanel showTopPanel = new TwitterJPanel(new BorderLayout());

        //JButtons showMessagesTotal, showPositivePercentage nested directly inside, inside showPanel
        TwitterJPanel showBottomPanel = new TwitterJPanel(new BorderLayout());

        //showTopPanel, showBottomPanel nested inside, inside outerEastPanel
        TwitterJPanel showPanel = new TwitterJPanel(new BorderLayout());

        //validInput, lastUpdated nested inside, inside
        TwitterJPanel validAndLastUpdatedPanel = new TwitterJPanel(new BorderLayout());

        //validAndLastUpdatedPanel, showTopPanel, inside
        TwitterJPanel adminPanel = new TwitterJPanel(new BorderLayout());



        TwitterJButton addUserButton = new TwitterJButton("Add User");
        TwitterJButton addGroupButton = new TwitterJButton("Add Group");
        TwitterJButton openUserViewButton = new TwitterJButton("Open User View");
        TwitterJButton showUserTotalButton = new TwitterJButton("Show User Total");
        TwitterJButton showGroupTotalButton = new TwitterJButton("Show Group Total");
        TwitterJButton showMessagesTotalButton = new TwitterJButton("Show Messages Total");
        TwitterJButton showPositivePercentageButton = new TwitterJButton("Show Positive Percentage");
        TwitterJButton validInputButton = new TwitterJButton("Show Invalid Input");
        TwitterJButton lastUpdateTimeButton = new TwitterJButton("Show Last Updated User");

        Window window = new Window();
        window.setViews(Arrays.asList(new View[]{tJFrame, addUserButton, addGroupButton, openUserViewButton,
                showUserTotalButton, showGroupTotalButton, showMessagesTotalButton,
                showPositivePercentageButton, treeViewPanel, addUserPanel, addGroupPanel, addPanel,
                outerEastPanel, showTopPanel, showBottomPanel, showPanel, validAndLastUpdatedPanel, adminPanel}));
        window.display();

        for (View v : AdminControlPanel.allRootViews) {
            v.display();
        }

        validAndLastUpdatedPanel.add(validInputButton, BorderLayout.EAST);
        validAndLastUpdatedPanel.add(lastUpdateTimeButton, BorderLayout.WEST);

        adminPanel.add(validAndLastUpdatedPanel, BorderLayout.NORTH);
        adminPanel.add(showTopPanel, BorderLayout.SOUTH);

        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Root");
        JTree userJTree = new JTree(root);
        userJTree.setScrollsOnExpand(true);
        userJTree.setPreferredSize(new Dimension(400, 500));
        final DefaultMutableTreeNode[] selectedNode = new DefaultMutableTreeNode[1];
        userJTree.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent treeSelectionEvent) {
                selectedNode[0] = (DefaultMutableTreeNode) userJTree.getLastSelectedPathComponent();
            }
        });
        DefaultMutableTreeNode firstLeaf = ((DefaultMutableTreeNode) userJTree.getModel().getRoot()).getFirstLeaf();
        userJTree.setSelectionPath(new TreePath(firstLeaf.getPath()));
        treeViewPanel.add(userJTree);
        outerMostPanel.add(outerEastPanel, BorderLayout.EAST);
        outerMostPanel.add(treeViewPanel, BorderLayout.WEST);

        JTextField addUserTextField = new JTextField(10);
        final String[] addUserText = {""};
        addUserTextField.getDocument().addDocumentListener(new DocumentListener() {
            //when change is made to text
            @Override
            public void insertUpdate(DocumentEvent documentEvent) {
                addUserText[0] = addUserTextField.getText();
                System.out.println(addUserText[0]);
            }

            //when field is cleared.
            @Override
            public void removeUpdate(DocumentEvent documentEvent) {
                addUserText[0] = addUserTextField.getText();
                System.out.println(addUserText[0]);
            }

            //not sure when this happens
            @Override
            public void changedUpdate(DocumentEvent documentEvent) {
                System.out.println("This happened");
            }
        });
        addUserButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (!addUserText[0].equals("")) {
                    if (UserRepository.getInstance().getUser(addUserText[0]) == null) {
                        if(addUserText[0].contains(" ")) {
                            InvalidInputCounter.getInstance().incrementCounter();
                        }
                        visitorCollection.accept(userCountVisitor);
                        DefaultTreeModel model = (DefaultTreeModel) userJTree.getModel();
                        DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
                        User newUser = new User(addUserText[0]);
                        UserRepository.getInstance().addUser(newUser);
                        model.insertNodeInto(new DefaultMutableTreeNode(newUser), selectedNode[0], selectedNode[0].getChildCount());
                    } else {
                        JOptionPane.showMessageDialog(null, "User name taken please enter a new unique name.");
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Please enter a user name.");
                }
            }
        });

        JTextField addGroupTextField = new JTextField(10);
        final String[] addGroupText = {""};
        addGroupTextField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent documentEvent) {
                addGroupText[0] = addGroupTextField.getText();
                System.out.println(addGroupText[0]);
            }

            @Override
            public void removeUpdate(DocumentEvent documentEvent) {
                addGroupText[0] = addGroupTextField.getText();
                System.out.println(addGroupText[0]);
            }

            @Override
            public void changedUpdate(DocumentEvent documentEvent) {
                addGroupText[0] = addGroupTextField.getText();
                System.out.println(addGroupText[0]);
            }
        });
        addGroupButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                if (!addGroupText[0].equals("")) {
                    if(addGroupText[0].contains(" ")) {
                        InvalidInputCounter.getInstance().incrementCounter();
                    }
                    visitorCollection.accept(groupCountVisitor);
                    DefaultTreeModel model = (DefaultTreeModel) userJTree.getModel();
                    DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
                    //model.insertNodeInto(new DefaultMutableTreeNode(addGroupText[0]), root, root.getChildCount());
                    model.insertNodeInto(new DefaultMutableTreeNode(addGroupText[0]), selectedNode[0], selectedNode[0].getChildCount());
                } else {
                    JOptionPane.showMessageDialog(null, "Please enter a group name.");
                }
            }
        });
        openUserViewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (selectedNode[0].getUserObject() instanceof String) {
                    JOptionPane.showMessageDialog(null, "Element attempting to open is not a user.");
                } else {
                    new UserViewPanel((User) selectedNode[0].getUserObject());
                }
            }
        });

        openUserViewPanel.add(openUserViewButton, BorderLayout.CENTER);

        showUserTotalButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JOptionPane.showMessageDialog(null, "User Total: " + userCountVisitor.getUserCounter());
            }
        });

        showGroupTotalButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JOptionPane.showMessageDialog(null, "Group Total: " + groupCountVisitor.getGroupCounter());
            }
        });

        showTopPanel.add(showUserTotalButton, BorderLayout.WEST);
        showTopPanel.add(showGroupTotalButton, BorderLayout.EAST);

        showMessagesTotalButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JOptionPane.showMessageDialog(null, "Messages Total: " + MessageCounter.getInstance().getTotalMessages());
            }
        });

        showPositivePercentageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (MessageCounter.getInstance().getTotalMessages() != 0)
                    JOptionPane.showMessageDialog(null, "Positive Percentage: " + MessageCounter.getInstance().positivePercentage() + "%");
                else
                    JOptionPane.showMessageDialog(null, "Positive Percentage: " + MessageCounter.getInstance().getTotalPositiveMessages() + "%");

            }
        });

        validInputButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JOptionPane.showMessageDialog(null, "Invalid Input Count: " + InvalidInputCounter.getInstance().getCounter());
            }
        });



        lastUpdateTimeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                for(User user : UserRepository.getInstance().getUserRepository().values()) {
                    lastUpdatedVisitor.visit(user);
                }
                JOptionPane.showMessageDialog(null, "Last Updated User: " + lastUpdatedVisitor.getUserLastUpdated());
            }
        });

        showBottomPanel.add(showMessagesTotalButton, BorderLayout.WEST);
        showBottomPanel.add(showPositivePercentageButton, BorderLayout.EAST);

        //showPanel.add(showTopPanel, BorderLayout.NORTH);
        showPanel.add(adminPanel, BorderLayout.NORTH);
        showPanel.add(showBottomPanel, BorderLayout.SOUTH);

        addUserPanel.add(addUserTextField, BorderLayout.WEST);
        addUserPanel.add(addUserButton, BorderLayout.EAST);

        addGroupPanel.add(addGroupButton, BorderLayout.EAST);
        addGroupPanel.add(addGroupTextField, BorderLayout.WEST);

        addPanel.add(addUserPanel, BorderLayout.NORTH);
        addPanel.add(addGroupPanel, BorderLayout.SOUTH);

        outerEastPanel.add(addPanel, BorderLayout.NORTH);
        outerEastPanel.add(openUserViewPanel, BorderLayout.CENTER);
        outerEastPanel.add(showPanel, BorderLayout.SOUTH);

        tJFrame.setContentPane(outerMostPanel);
        tJFrame.pack();
        tJFrame.setVisible(true);
    }

    /**
     * Create the GUI and show it using Singleton pattern.
     */
    public static AdminControlPanel getInstance() {
        if (instance == null) {
            instance = new AdminControlPanel();
        }
        return instance;
    }

}
