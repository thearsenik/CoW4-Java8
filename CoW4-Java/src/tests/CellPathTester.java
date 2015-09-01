package tests;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import java.util.Stack;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JFrame;

import fr.ttfx.cow4.world.Cell;
import fr.ttfx.cow4.world.StaticGameWorld;



public class CellPathTester {

	private static Cell start;
	
	private static Cell end;
	
	private static List<Cell> path;
	
	private static final StaticGameWorld game = new StaticGameWorld();

	public static void main(String[] args) throws Exception {
		
		final JFrame frame = new JFrame("Path Tester");
		frame.setSize(600, 600);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);

		final BufferedImage map = ImageIO.read(new File("assets/map.png"));
		
		final JComponent panel = new JComponent() {

			final BasicStroke stroke = new BasicStroke(3);
			
			{
				addMouseListener(new MouseAdapter() {
					
					public void mouseClicked(MouseEvent e) {
						
						final int x = (e.getX() - 4) / 20;
						final int y = (e.getY() - 4) / 20;
						
						if(x >= 0 && x <= 24 && y >= 0 && y <= 24) {
							
							if((e.getModifiersEx() & MouseEvent.SHIFT_DOWN_MASK) == MouseEvent.SHIFT_DOWN_MASK) {
								start = game.getCell(y, x);
								path = game.getShortestPath(start, end);
								repaint();
								
							} else if((e.getModifiersEx() & MouseEvent.ALT_DOWN_MASK) == MouseEvent.ALT_DOWN_MASK) {
								end = game.getCell(y, x);
								path = game.getShortestPath(start, end);
								repaint();
							}
						}
					}					
				});
			}
			
			@Override
			public void paint(Graphics g1) {
				super.paint(g1);
				final Graphics2D g = (Graphics2D) g1;
				g.drawImage(map, 0, 0, null);
				
				if(path != null) {
					g.setStroke(stroke);
					g.setColor(Color.GREEN.darker());
					
					Cell last = null;
					for(Cell cell : path) {
						if(last != null) {
							g.drawLine(last.getColumn() * 20 + 10 + 5, last.getLine() * 20 + 10 + 5, 
									cell.getColumn() * 20 + 10 + 5, cell.getLine() * 20 + 10 + 5);
						}
						last = cell;
					}
				}
				
				if(start != null) {
					g.setColor(Color.BLUE);
					g.fillOval(start.getColumn() * 20 + 5, start.getLine() * 20 + 5, 15, 15); 
				}
				
				if(end != null) {
					g.setColor(Color.ORANGE);
					g.fillOval(end.getColumn() * 20 + 5, end.getLine() * 20 + 5, 15, 15); 
				}

			}
			
		};
		panel.setSize(map.getWidth(), map.getHeight());
		frame.setContentPane(panel);;
		frame.setVisible(true);
	}

}
