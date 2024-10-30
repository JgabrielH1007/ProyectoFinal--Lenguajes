/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Backend;

import java.awt.BorderLayout;
import java.awt.HeadlessException;
import java.util.List;
import javax.swing.JScrollPane;

/**
 *
 * @author gabrielh
 */
public class ReporteErroresLexico extends javax.swing.JFrame {

    public ReporteErroresLexico(List<Token> listaErrores) {
        
        this.setLayout(new BorderLayout());

        this.setSize(800, 600);
        this.setVisible(true);  
        this.revalidate();     
        this.repaint();         
    }
    
    
    
    
}
