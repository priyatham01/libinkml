/*
 * Created on 23.07.2007
 *
 * Copyright (C) 2007  Emanuel Indermühle <emanuel@inthemill.ch>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * @author emanuel
 */

package ch.unibe.eindermu.utils;

import java.awt.GraphicsConfiguration;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.plaf.metal.MetalBorders;
import javax.swing.plaf.metal.MetalLookAndFeel;


public class ConfigView extends JFrame implements Observable{
    
    private static final long serialVersionUID = 1L;

	public static final Aspect ON_ANNO_TYPES_CHANGED = new Aspect(){};
    
    private JPanel jContentPane = null;
    
    private JTextField jTextField = null;
    
    private JList jList = null;
    
    private JButton jButton = null;
    
    private JButton jButton1 = null;
    
    private AbstractObservable observerSupport;
    
    private Config c;
    
    /**
     * This is the default constructor
     */
    
    public ConfigView(GraphicsConfiguration gc, Config c) {
    	super(gc);
    	this.c = c;
        initialize();
        ((JComponent) this.getContentPane()).setBorder(new TitledBorder(new LineBorder(MetalLookAndFeel.getControlShadow()), "Annotation Types"));
        this.observerSupport = new AbstractObservable();
    }
    
    /**
     * This method initializes this
     * 
     * @return void
     */
    private void initialize() {
        this.setSize(282, 175);
        this.setContentPane(getJContentPane());
        this.setTitle("Preferences");
    }
    
    /**
     * This method initializes jContentPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getJContentPane() {
        if(jContentPane == null) {
            GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
            gridBagConstraints5.gridx = 1;
            gridBagConstraints5.anchor = GridBagConstraints.WEST;
            gridBagConstraints5.insets = new Insets(6, 6, 0, 6);
            gridBagConstraints5.gridy = 3;
            GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
            gridBagConstraints4.gridx = 0;
            gridBagConstraints4.anchor = GridBagConstraints.WEST;
            gridBagConstraints4.insets = new Insets(6, 12, 0, 6);
            gridBagConstraints4.gridy = 3;
            GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
            gridBagConstraints3.fill = GridBagConstraints.BOTH;
            gridBagConstraints3.gridy = 2;
            gridBagConstraints3.weightx = 1.0;
            gridBagConstraints3.weighty = 1.0;
            gridBagConstraints3.gridheight = 4;
            gridBagConstraints3.insets = new Insets(12, 6, 12, 13);
            gridBagConstraints3.gridx = 3;
            
            GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
            gridBagConstraints2.fill = GridBagConstraints.BOTH;
            gridBagConstraints2.gridy = 2;
            gridBagConstraints2.weightx = 0.0;
            gridBagConstraints2.gridwidth = 2;
            gridBagConstraints2.insets = new Insets(12, 12, 6, 6);
            gridBagConstraints2.gridx = 0;
            jContentPane = new JPanel();
            jContentPane.setLayout(new GridBagLayout());
            jContentPane.add(getJTextField(), gridBagConstraints2);
            jContentPane.add(getJList(), gridBagConstraints3);
            jContentPane.add(getJButton(), gridBagConstraints4);
            jContentPane.add(getJButton1(), gridBagConstraints5);
        }
        return jContentPane;
    }
    
    /**
     * This method initializes jTextField
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getJTextField() {
        if(jTextField == null) {
            jTextField = new JTextField();
        }
        return jTextField;
    }
    
    /**
     * This method initializes jList
     * 
     * @return javax.swing.JList
     */
    private JList getJList() {
        if(jList == null) {
            jList = new JList();
            jList.setModel(new ConfigListModel());
            // jList.setBorder(new
            // LineBorder(MetalLookAndFeel.getControlShadow()));
            jList.setBorder(MetalBorders.getTextFieldBorder());
        }
        return jList;
    }
    
    private class ConfigListModel implements ListModel{
        private Vector<ListDataListener> ls = new Vector<ListDataListener>();
        
        public void addListDataListener(ListDataListener l) {
            ls.add(l);
        }
        
        public Object getElementAt(int index) {
            return c.get("annotypes").split(" ")[index];
        }
        
        public int getSize() {
            return c.get("annotypes").split(" ").length;
        }
        
        public void removeListDataListener(ListDataListener l) {
            ls.remove(l);
        }
        
        public void remove(int i) {
            ListDataEvent e = new ListDataEvent(this, ListDataEvent.INTERVAL_REMOVED, i, i);
            for(ListDataListener l : this.ls) {
                l.intervalRemoved(e);
            }
        }
        
        public void add(int i) {
            ListDataEvent e = new ListDataEvent(this, ListDataEvent.INTERVAL_ADDED, i, i);
            for(ListDataListener l : this.ls) {
                l.intervalRemoved(e);
            }
        }
    }
    
    /**
     * This method initializes jButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getJButton() {
        if(jButton == null) {
            jButton = new JButton();
            jButton.setText("add");
            jButton.addActionListener(new java.awt.event.ActionListener(){
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    String add = ConfigView.this.jTextField.getText();
                    if(add == "") {
                        return;
                    }
                    String res = c.get("annotypes") + " " + add;
                    c.set("annotypes", res.trim());
                    ((ConfigListModel) ConfigView.this.jList.getModel()).add(c.get("annotypes").split(" ").length - 1);
                    observerSupport.notifyObserver(ON_ANNO_TYPES_CHANGED);
                    try {
                    	c.save();
                    } catch(FileNotFoundException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    } catch(IOException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                }
            });
        }
        return jButton;
    }
    
    /**
     * This method initializes jButton1
     * 
     * @return javax.swing.JButton
     */
    private JButton getJButton1() {
        if(jButton1 == null) {
            jButton1 = new JButton();
            jButton1.setText("remove");
            jButton1.addActionListener(new java.awt.event.ActionListener(){
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    String removed = (String) ConfigView.this.jList.getSelectedValue();
                    if(removed == null) {
                        return;
                    }
                    String res = "";
                    for(String s : c.get("annotypes").split(" ")) {
                        if(!s.equals(removed)) {
                            res = res + s + " ";
                        }
                    }
                    c.set("annotypes", res.trim());
                    observerSupport.notifyObserver(ON_ANNO_TYPES_CHANGED);
                    ((ConfigListModel) ConfigView.this.jList.getModel()).remove(ConfigView.this.jList.getSelectedIndex());
                    try {
                    	c.save();
                    } catch(FileNotFoundException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    } catch(IOException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                }
            });
        }
        return jButton1;
    }
    
    public void registerFor(Aspect event, Observer o) {
        this.observerSupport.registerFor(event, o);
    }
    
} // @jve:decl-index=0:visual-constraint="10,10"
