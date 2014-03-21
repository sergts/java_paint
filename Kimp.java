package myKimp;

import java.awt.BorderLayout;
import java.awt.Color;

import java.awt.Dimension;

import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;

import javax.swing.JFrame;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;



public class Kimp extends JFrame {
	

	
	
class Shape{
		
		
			
		public Color color = currentColor;
		public boolean selected;
		public boolean removed;   //If shape should not be displayed.
		public boolean thickShape;  
		
		public ArrayList<Point> points = new ArrayList<Point>();
		
		public void addPoint(Point p) {
			points.add(p);
			
		}
		
		public ArrayList<Point> getPoints() {
			return points;
		}
		
		public void move(int dx, int dy) {
			for(Point p : points){
				p.x += dx;
				p.y += dy;
			}
			
		}
		
		public void paint(Graphics g) {
			
			if (selected) {
				g.setColor(Color.RED);
			} else {
				g.setColor(color);
			}
			
			Point prev = null;
			
			if(!removed){
				
				for (Point p : points) {
					if (prev == null) {
						prev = p;
						continue;
					}
					
					if(thickShape)
						//g.fillOval(p.x, p.y, (int)(Math.random()*15), (int)(Math.random()*15)); 
						g.fillOval(p.x, p.y, 15, 15);
					
					else
						g.drawLine(prev.x, prev.y, p.x, p.y);
					
					prev = p;
				}
			}
		}

		
		public Shape copy() {
			
			Shape s  = new Shape();
			s.selected = selected;
			s.removed = removed;
			s.color = color;
			s.thickShape = thickShape;
			
			for(Point point : points){
				s.addPoint( new Point(point) );
			}
			repaint();
			return s;
		}

	
		public int getDistance(Point p) {
			
			double min = Double.POSITIVE_INFINITY;
			
			for(Point point : points){
				if(p.distance(point) < min){
					min = p.distance(point);
				}
				
				
			}
			return (int)min;
		}
		
	}


	
	
	
	class ShapeList {
		
		ArrayList<ArrayList<Shape>> shapes = new ArrayList<ArrayList<Shape>>();
		
		
		public ShapeList() {
			shapes.add(new ArrayList<Shape>());
		}
		
		public ArrayList<Shape> getShapes() {
			return shapes.get(shapes.size() - 1);
		}
		
		public void addShape(Shape s) {
			newState();
			shapes.get(shapes.size() - 1).add(s);
		}
		
		public void newState() {
			ArrayList<Shape> newState = new ArrayList<Shape>();
			for (Shape s : getShapes()) {
				newState.add(s.copy());
			}
			shapes.add(newState);
		}
		
		public Shape getLastShape() {
			int size = shapes.get(shapes.size() - 1).size();
			if ( size > 0) {
				return shapes.get(shapes.size() - 1).get(size - 1);
			}
			return null;
		}
		
		public Shape getSelectedShape() {
			for (Shape s : getShapes()) {
				if (s.selected) return s;
			}
			return null;
		}
		
		public boolean undo() {
			if (shapes.size() > 1) {
				shapes.remove(shapes.size() - 1);
				return true;
			}
			return false;
		}
		
	
		
		public void select(Point p) {
			unselectAll();
			Shape closestShape = null;
			int min = 150;
			for (Shape s : getShapes()) {
				int dist = s.getDistance(p);
				if (dist < min) {
					min = dist;
					closestShape = s;
				}
			}
			if (closestShape != null && !closestShape.removed) {
				closestShape.selected = true;
			}
		}
		
		public void unselectAll() {
			for (Shape s : getShapes()) {
				s.selected = false;
			}
		}
	}
	
	
	public ShapeList shapeList = new ShapeList();
	public boolean selectMode = false;
	public Point lastPoint = null;
	public Color currentColor = Color.black;
	public int currentColorInt = 0;
	
	
	public static void main(String[] args) {
		Kimp k = new Kimp();
	}
	
	public DrawingArea area = new DrawingArea();
	
	
	public Kimp() {
		
		area.setBackground(Color.WHITE);
		
		this.setTitle("Kimp");
		this.setSize(800, 600);
		this.setLayout(new GridLayout());
		
		JPanel content = new JPanel();
		content.setLayout(new BorderLayout(5, 5));
		content.add(area);
		
		this.setContentPane(content);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		pack();
		
		this.setVisible(true);
		
		area.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				super.mousePressed(e);
				
				if (selectMode) {
					
					if(SwingUtilities.isRightMouseButton(e)){
						shapeList.newState();
						Shape s = shapeList.getSelectedShape();
						if(s != null){
							
							s.removed = true;
							lastPoint = e.getPoint();
						}
						else
							shapeList.undo();
						
					}
					
						
					else{	
						
					   shapeList.newState();    
					   lastPoint = e.getPoint();  
					}
					 
					
				} 
				
				else {
					if (SwingUtilities.isLeftMouseButton(e)) {
						
						
				         if( e.getX() < 53 && e.getY() < (getHeight()/2) ){
				        	 changeColor(e.getY());
				         }
				         else{
							Shape s = new Shape();
							s.addPoint(e.getPoint());
							shapeList.addShape(s);
				         }
					} 
					
					else {
						Shape s = new Shape();
						s.thickShape = true;
						s.addPoint(e.getPoint());
						shapeList.addShape(s);
					}
					repaint();
				}
			}
		});
		
		area.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				super.mouseDragged(e);
				
				if (selectMode) {
					Shape s = shapeList.getSelectedShape();
					if (s != null) {
						s.move(e.getPoint().x - lastPoint.x, e.getPoint().y - lastPoint.y);
						repaint();
					}
					lastPoint = e.getPoint();
				} else  {
					
					 if( !( e.getX() < 53 && e.getY() < (getHeight()/2)) ){
						 Shape s = shapeList.getLastShape();
							if (s != null) {
								s.addPoint(e.getPoint());
								repaint();
							}
			         }
					
				} 
			}
			
			@Override
			public void mouseMoved(MouseEvent e) {
				// TODO Auto-generated method stub
				super.mouseMoved(e);
				
				if (selectMode) {
					shapeList.select(e.getPoint());
					repaint();
				}
			}
		});
		
		this.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				super.keyPressed(e);
				
				if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
					
					if (!shapeList.undo()) {
						if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
							int result = JOptionPane.showConfirmDialog(null, "Do you want to exit?", "EXIT",  JOptionPane.YES_NO_OPTION);
							if (result == JOptionPane.YES_OPTION) {
								System.exit(0);
							}
						}
					}
					
					repaint();
				}
				
				
				
				if (e.getKeyCode() == KeyEvent.VK_CONTROL) {
					selectMode = true;
				}
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				super.keyReleased(e);
				if (e.getKeyCode() == KeyEvent.VK_CONTROL || e.getKeyCode() == KeyEvent.VK_ESCAPE) {
					selectMode = false;
					shapeList.unselectAll();
					repaint();
				} 
			}
		});
	}
	
	
    private void changeColor(int y) {
        
        
        int height = area.getHeight();         // Height of panel.
        int colorSpacing = (height) / 10;  // Space for one color rectangle.
        int newColor = y / colorSpacing;       // Which color number was clicked?
        
        
        
        if (newColor < 0 || newColor > 4)      
           return;
        
        
        
        switch (newColor){
        case 0:
        	currentColor = Color.BLACK;
        	currentColorInt = 0;
        	break;
        case 1:
        	currentColor = Color.ORANGE;
        	currentColorInt = 1;
        	break;
        case 2:
        	currentColor = Color.GREEN;
        	currentColorInt = 2;
        	break;
        case 3:
        	currentColor = Color.BLUE;
        	currentColorInt = 3;
        	break;
        case 4:
        	currentColor = Color.CYAN;
        	currentColorInt = 4;
        	break;
       
        }
        
     }
	
	
	
	
	class DrawingArea extends JPanel{
		
	
        
        
        
		
		
		public DrawingArea() {
	        
	        setPreferredSize(new Dimension(800,600));
	    }
		
		
		
		@Override
		public void paintComponent(Graphics g) {
	        super.paintComponent(g);  
	        
	        
	       
	        int height = getHeight();  // Height of the panel.
	        
	        int colorSpacing = (height) / 10;
	        
	       
	      
	        
	        
	        
	        for (Shape s : shapeList.getShapes()) {
				s.paint(g);
			}
	        
	         g.setColor(Color.BLACK);
	         g.fillRect(2, 1 + 0*colorSpacing, 50, colorSpacing-1);   //creates a palette 
	         g.setColor(Color.ORANGE);
	         g.fillRect(2, 1 + 1*colorSpacing, 50, colorSpacing-1);
	         g.setColor(Color.GREEN);
	         g.fillRect(2, 1 + 2*colorSpacing, 50, colorSpacing-1);
	         g.setColor(Color.BLUE);
	         g.fillRect(2, 1 + 3*colorSpacing, 50, colorSpacing-1);
	         g.setColor(Color.CYAN);
	         g.fillRect(2, 1 + 4*colorSpacing, 50, colorSpacing-1);
	        
	         g.setColor(Color.GRAY);
	         g.drawRect(2, 1 + currentColorInt*colorSpacing, 52, colorSpacing);  
	         g.drawRect(2, 2 + currentColorInt*colorSpacing, 51, colorSpacing-1); //shows which color is being used
	        
	    }
		
		
	}
	
}