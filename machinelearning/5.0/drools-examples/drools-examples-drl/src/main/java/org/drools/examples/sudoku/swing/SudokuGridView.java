/*
 * JBoss, the OpenSource J2EE webOS
 * 
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.drools.examples.sudoku.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GridLayout;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class SudokuGridView
   extends JComponent
   implements SudokuGridListener, ComponentListener
{
   /** The serialVersionUID */
   private static final long serialVersionUID = 1L;
   private SudokuGridModel model;
   private GridLayout gridLayout;
   private JTextField textFields[][];
   
   public SudokuGridView()
   {
      gridLayout = new GridLayout(SudokuGridModel.NUM_ROWS, SudokuGridModel.NUM_COLS);
      setLayout(gridLayout);
      textFields = new JTextField[SudokuGridModel.NUM_ROWS][SudokuGridModel.NUM_COLS];
      for (int row=0; row<textFields.length; row++)
      {
         for (int col=0; col<textFields[row].length; col++)
         {
            JTextField textField = new JTextField("");
            textField.setEditable(false);
            if (row==0 && col==0)
            {
               textField.addComponentListener(this);
            }
            JPanel panel = new JPanel();
            
            int top = row % SudokuGridModel.INNER_GRID_HEIGHT == 0 ? 2 : 0;
            int left = col % SudokuGridModel.INNER_GRID_WIDTH == 0 ? 2 : 0;
            int bottom = row == SudokuGridModel.NUM_ROWS-1 ? 2 : 0;
            int right = col == SudokuGridModel.NUM_COLS-1 ? 2 : 0;
           
            panel.setBorder(BorderFactory.createMatteBorder( top, left, bottom, right, Color.BLACK ));
            panel.setLayout(new BorderLayout());
            
            textField.setOpaque(true);
            textField.setBackground(Color.WHITE);
            textField.setHorizontalAlignment(JTextField.CENTER);
            
            textFields[row][col] = textField;
            
            panel.add(BorderLayout.CENTER, textField);
            add(panel);
         }
      }
   }
   
   public SudokuGridView(SudokuGridModel model)
   {
      this();
      setModel(model);
   }
   
   public void setModel(SudokuGridModel model)
   {
      if (this.model != null  && this.model instanceof AbstractSudokuGridModel)
      {
         ((AbstractSudokuGridModel) this.model).removeSudokuGridListener(this);
      }
      
      this.model = model;
      refreshValues();
      
      if (this.model != null  && this.model instanceof AbstractSudokuGridModel)
      {
         ((AbstractSudokuGridModel) this.model).addSudokuGridListener(this);
      }
   }
   
   public SudokuGridModel getModel()
   {
      return model;
   }
   
   private void refreshValues()
   {
      for (int row=0; row<textFields.length; row++)
      {
         for (int col=0; col<textFields[row].length; col++)
         {
            refreshValue(row, col);
         }
      }      
   }
   
   private void refreshValue(int row, int col)
   {
      if(getModel().isCellResolved(row, col))
      {
         textFields[row][col].setText(""+getModel().getPossibleCellValues(row, col).iterator().next());
      }
      else
      {
         textFields[row][col].setText("");
      }      
   }

   public void cellResolved(SudokuGridEvent ev)
   {
      refreshValue(ev.getRow(), ev.getCol());
   }

   public void cellModified(SudokuGridEvent ev)
   {
      refreshValue(ev.getRow(), ev.getCol());
   }
   
   public void componentHidden(ComponentEvent ev)
   {
      // FIXME componentHidden
      
   }

   public void componentMoved(ComponentEvent ev)
   {
      // FIXME componentMoved
      
   }

   public void componentResized(ComponentEvent ev)
   {
      JTextField textField = (JTextField) ev.getComponent();
      int height = textField.getHeight();
      Font font = textField.getFont();
      
      FontMetrics fontMetrics = textField.getGraphics().getFontMetrics(font);
      if (fontMetrics.getAscent() < height)
      {
         while (fontMetrics.getAscent() < height)
         {
            font = new Font(font.getName(), font.getStyle(), font.getSize()+2);
            fontMetrics = textField.getGraphics().getFontMetrics(font);
         }
      }
      else if (fontMetrics.getAscent() > height)
      {
         while (fontMetrics.getAscent() > height)
         {
            font = new Font(font.getName(), font.getStyle(), font.getSize()-2);
            fontMetrics = textField.getGraphics().getFontMetrics(font);
         }         
      }
      else
      {
         //
      }
      
      for (int row=0; row<textFields.length; row++)
      {
         for (int col=0; col<textFields[row].length; col++)
         {
            textFields[row][col].setFont(font);
         }
      }
   }

   public void componentShown(ComponentEvent ev)
   {
      // FIXME componentShown
   }
}
