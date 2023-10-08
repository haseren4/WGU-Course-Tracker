/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package wgu.course.tracker;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.ParsingException;
import java.text.SimpleDateFormat;
import javax.swing.JTable;

/**
 *
 * @author haser
 */
public class MainWindow extends javax.swing.JFrame implements WindowListener{
    public static String XML_FILEPATH = "WGU-Course-Tracker-Data.xml";
    public static String XML_COURSEROOT = "COURSE";
    public static String XML_COURSENAME = "courseName";
    public static String XML_COURSECOMP = "courseComp";
    public static String XML_COURSECRED = "courseCred";
    public static String XML_COURSEDATE = "courseDate";
    public static String XML_STARTTERM = "START";
    public static String XML_ENDTERM = "END";
    
    int allCredits = 0;
    int compCredits = 0;
        
    /**
     * Creates new form MainWindow
     */
    public MainWindow() {
        initComponents();
        addWindowListener(this);
        courseTable.getModel().addTableModelListener(new TableModelListener(){
            @Override
            public void tableChanged(TableModelEvent e) {
                paintChanges();
            }
        });
        
        fillIn();
        paintChanges();
        
        
    }
    public void fillIn(){
        try{
            File xmlFile = new File(XML_FILEPATH);
            if(xmlFile.exists()){
                Builder parser = new Builder();
                Document doc = parser.build(xmlFile);
                Element root = doc.getRootElement();
                Elements children = root.getChildElements(XML_COURSEROOT);
                Element endElement = root.getFirstChildElement(XML_ENDTERM);
                termEndTbf.setText( endElement.getValue());
                Element startElement = root.getFirstChildElement(XML_STARTTERM);
                termStartTbf.setText(startElement.getValue());
                for(int i =0; i < children.size();i++){

                    courseTable.setValueAt(children.get(i).getChildElements(XML_COURSENAME).get(0).getValue(),
                            i,
                            0);
                    courseTable.setValueAt(children.get(i).getChildElements(XML_COURSECRED).get(0).getValue(),
                            i,
                            1);
                    courseTable.setValueAt(children.get(i).getChildElements(XML_COURSECOMP).get(0).getValue(),
                            i,
                            2);
                    courseTable.setValueAt(children.get(i).getChildElements(XML_COURSEDATE).get(0).getValue(),
                            i,
                            3);
                    
                }
                lockPanelTgl.setSelected(true);
            }
            
        }
        catch(ParsingException e){}
        catch(IOException e){}
        
        
    }
    
    public void paintChanges(){
        allCredits = 0;
        compCredits = 0;
        int remainingCourses = 0;
        for(int i = 1; i < courseTable.getRowCount(); i++){
                if(courseTable.getValueAt(i-1, 1) != null){
                    if(courseTable.getValueAt(i-1, 1) != "")
                        allCredits = allCredits + Integer.parseInt(courseTable.getValueAt(i-1, 1).toString());
                    if(courseTable.getValueAt(i-1, 2) != null)
                        if(courseTable.getValueAt(i-1, 2).toString().contains("yes")){
                            compCredits = compCredits + Integer.parseInt(courseTable.getValueAt(i-1, 1).toString());
                        }
                        else{
                            remainingCourses++;
                        }
                    
                    
                    
                    }
                
        }
        if(allCredits != 0){
            creditProgress.setValue(compCredits);
            creditProgress.setMaximum(allCredits);
            creditProgress.setToolTipText(compCredits + " / " + allCredits);
            
            System.out.println(compCredits + "/" + allCredits);
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date nowDate = new Date();
            System.out.println(nowDate.toString());
            Date startDate = sdf.parse(termStartTbf.getText());
            System.out.println(startDate.toString());
            Date endDate = sdf.parse(termEndTbf.getText());
            System.out.println(endDate.toString());
            
            long daysBetweenTerm = (endDate.getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24);
            long daysBetween = (endDate.getTime() - nowDate.getTime()) / (1000 * 60 * 60 * 24);
            termProgress.setMaximum((int) daysBetweenTerm);
            int prog = (int) ( daysBetweenTerm - daysBetween);
            if(prog < 0)
                termProgress.setValue(0);
            else{
                termProgress.setValue(prog);
            }
            if(remainingCourses > 0){
                coursePerDayLbl.setText("" + (int) daysBetween/remainingCourses +" days/course");
                termProgress.setToolTipText(daysBetween + " days left.");
            }
            else{
                coursePerDayLbl.setText("No Courses to Do");
            }
            
            
            System.out.println(prog +"/" + daysBetweenTerm);
        } catch (ParseException ex) {
            Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
        
         
    }
    
    public void beforeClose() throws IOException{
        File xmlFile = new File(XML_FILEPATH);
        if(!xmlFile.exists()){
            try{
                xmlFile.createNewFile();
                }
            catch(IOException e){
                System.out.println("IO EXCEPTION THROWN");
                e.printStackTrace(System.out);
            }
        }
        Element rootElement = new Element("root");
        Element startTerm = new Element(XML_STARTTERM);
        startTerm.appendChild(termStartTbf.getText());
        Element endTerm = new Element(XML_ENDTERM);
        endTerm.appendChild(termEndTbf.getText());
        rootElement.appendChild(startTerm);
        rootElement.appendChild(endTerm);
                for(int i = 1; i < courseTable.getRowCount(); i++){
                    Element courseRoot = new Element(XML_COURSEROOT);
                    
                    Element courseName = new Element(XML_COURSENAME);
                    Element courseComp = new Element(XML_COURSECOMP);
                    Element courseCred = new Element(XML_COURSECRED);
                    Element courseDate = new Element(XML_COURSEDATE);
    
                    if(courseTable.getValueAt(i-1, 0)!=null)
                        courseName.appendChild(courseTable.getValueAt(i-1, 0).toString());
                    if(courseTable.getValueAt(i-1, 1)!=null)
                        courseCred.appendChild(courseTable.getValueAt(i-1, 1).toString());
                    if(courseTable.getValueAt(i-1, 2)!=null)
                        courseComp.appendChild(courseTable.getValueAt(i-1, 2).toString());
                    if(courseTable.getValueAt(i-1, 3)!=null)
                        courseDate.appendChild(courseTable.getValueAt(i-1, 3).toString());
                    
                    if(courseTable.getValueAt(i-1,0)!=null){
                        courseRoot.appendChild(courseName);
                        courseRoot.appendChild(courseComp);
                        courseRoot.appendChild(courseCred);
                        courseRoot.appendChild(courseDate);
                        
                        rootElement.appendChild(courseRoot);
                    }
                    
                }
                
                Document doc = new Document(rootElement);
                
                FileWriter writer = new FileWriter(xmlFile);
                writer.write(doc.toXML());
                writer.close();
                System.out.println("XML Saved Successfully");
            
        
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        lockPanelTgl = new javax.swing.JToggleButton();
        termProgress = new javax.swing.JProgressBar();
        creditProgress = new javax.swing.JProgressBar();
        termStartTbf = new javax.swing.JTextField();
        termEndTbf = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        coursePerDayLbl = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        courseTable = new javax.swing.JTable();
        jLabel4 = new javax.swing.JLabel();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("WGU Course Tracker");

        lockPanelTgl.setText("Lock Panel");
        lockPanelTgl.setName("lockPanelTgl"); // NOI18N
        lockPanelTgl.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                lockPanelTglStateChanged(evt);
            }
        });

        termProgress.setStringPainted(true);

        creditProgress.setStringPainted(true);

        termStartTbf.setToolTipText("YYYY-MM-DD");

        termEndTbf.setToolTipText("YYYY-MM-DD");

        jLabel1.setText("Term Start");

        jLabel2.setText("Term End");

        jLabel3.setText("Credits");

        jLabel6.setText("Term Progress");

        coursePerDayLbl.setText("Need Courses and End Date");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(creditProgress, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel3))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(termStartTbf, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(termEndTbf, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(coursePerDayLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(lockPanelTgl))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(termProgress, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(termProgress, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 18, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(creditProgress, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(termStartTbf, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(termEndTbf, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(lockPanelTgl)
                    .addComponent(coursePerDayLbl)))
        );

        courseTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Course Name", "Credits", "Completion Comment", "Date"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Integer.class, java.lang.String.class, java.lang.Object.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        courseTable.addInputMethodListener(new java.awt.event.InputMethodListener() {
            public void caretPositionChanged(java.awt.event.InputMethodEvent evt) {
                courseTableCaretPositionChanged(evt);
            }
            public void inputMethodTextChanged(java.awt.event.InputMethodEvent evt) {
            }
        });
        jScrollPane2.setViewportView(courseTable);

        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("Courses");
        jLabel4.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel4.setVerifyInputWhenFocusTarget(false);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 982, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(filler1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(jLabel4)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 668, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addGap(281, 281, 281)
                        .addComponent(filler1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void courseTableCaretPositionChanged(java.awt.event.InputMethodEvent evt) {//GEN-FIRST:event_courseTableCaretPositionChanged
        // TODO add your handling code here:
        paintChanges();
    }//GEN-LAST:event_courseTableCaretPositionChanged

    private void lockPanelTglStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_lockPanelTglStateChanged
        
        if(lockPanelTgl.isSelected()){
            termEndTbf.enable(false);
            termStartTbf.enable(false);
            lockPanelTgl.setText("Panel Locked");
        }
        else{
            termEndTbf.enable(true);
            termStartTbf.enable(true);
            lockPanelTgl.setText("Lock Panel");
            
        }
    }//GEN-LAST:event_lockPanelTglStateChanged

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainWindow().setVisible(true);
            }
        });
    }
    @Override
     public void windowClosing(WindowEvent e) {
         System.out.println("Closing the Window");
        try {
            beforeClose();
        } catch (IOException ex) {
            Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel coursePerDayLbl;
    public javax.swing.JTable courseTable;
    private javax.swing.JProgressBar creditProgress;
    private javax.swing.Box.Filler filler1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JToggleButton lockPanelTgl;
    private javax.swing.JTextField termEndTbf;
    private javax.swing.JProgressBar termProgress;
    private javax.swing.JTextField termStartTbf;
    // End of variables declaration//GEN-END:variables

    @Override
    public void windowOpened(WindowEvent e) {
    }

    @Override
    public void windowClosed(WindowEvent e) {
    }

    @Override
    public void windowIconified(WindowEvent e) {
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
    }

    @Override
    public void windowActivated(WindowEvent e) {
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
    }
}
