/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bugapp.gui;

import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author Henrique
 */
public class SurveyCellRenderer extends DefaultTableCellRenderer {
    
    private static JLabel lblPending = null;
    private static JLabel lblAnswered = null;
    private static JLabel lblExcluded = null;
    private static JLabel lblSent = null;
    private static JLabel lblOther = null;
    
    public SurveyCellRenderer(){
        super();
    }
    
    @Override
    public java.awt.Component getTableCellRendererComponent(javax.swing.JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        
        String strValue = value.toString().toLowerCase();

        try{
            switch (strValue) {
                case "p":
                    if (lblPending == null) {
                        lblPending = getImageLabel(strValue);
                        lblPending.setToolTipText("Pending");
                    }
                    return lblPending;

                case "a":
                    if (lblAnswered == null) {
                        lblAnswered = getImageLabel(strValue);
                        lblAnswered.setToolTipText("Answered");
                    }
                    return lblAnswered;

                case "e":
                    if (lblExcluded == null) {
                        lblExcluded = getImageLabel(strValue);
                        lblExcluded.setToolTipText("Excluded");
                    }
                    return lblExcluded;

                case "s":
                    if (lblSent == null) {
                        lblSent = getImageLabel(strValue);
                        lblSent.setToolTipText("Sent");
                    }
                    return lblSent;

                case "o":
                    if (lblOther == null) {
                        lblOther = getImageLabel(strValue);
                        lblOther.setToolTipText("Other");
                    }
                    return lblOther;

                default:
                    if (lblOther == null) {
                        lblOther = getImageLabel(strValue);
                        lblOther.setToolTipText("Other");
                    }
                    return lblOther;
            }

//        String ResourcePath = "./resources/status-"+strValue+"-icon.png";
//        
//        try {
//            BufferedImage bimg = ImageIO.read(this.getClass().getResourceAsStream(ResourcePath));
//            ImageIcon iic = new ImageIcon(bimg);
//            JLabel lbl = new JLabel(iic);
//            return lbl;
//            
        } catch (IOException ex) {
            System.out.println(ex);
            return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        }
    }
    
    private JLabel getImageLabel(String Status) throws IOException{
        //Path = this.getClass().getResource("/br/pucminas/dcc/jpacs/resources/novo.png");        
        String ResourcePath = "/bugapp/gui/resources/status-" + Status + "-icon.png";
        //BufferedImage bimg = ImageIO.read(this.getClass().getResource(ResourcePath));
        ImageIcon iic = new ImageIcon(this.getClass().getResource(ResourcePath));
        JLabel lbl = new JLabel(iic);
        return lbl;
    }
    
}
