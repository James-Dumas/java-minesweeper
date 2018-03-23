import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Minesweeper extends JFrame {
	private JFrame mainFrame;
	private JPanel drawPanel;
	private Board board;
	
	private int boardSize = 20;
	private double difficulty = 0.18;
	private int scale = 30;
	private int padding = 10;
	private int screenW = 620;
	private int screenH = 690;
	
	private boolean lose;
	private boolean win;
	
	private int[] hintTile;
	
	public static void main(String[] args)
	{
		Minesweeper game = new Minesweeper();
		game.init_gui();
		game.new_game();
	}
	
	public void init_gui()
	{
		// set up and show GUI
		
		mainFrame = new JFrame("Java Minesweeper");
		mainFrame.setSize(screenW, screenH);
	    mainFrame.setDefaultCloseOperation(EXIT_ON_CLOSE);
	    mainFrame.setResizable(false);
	    
	    drawPanel = new GamePanel();
	    drawPanel.addMouseListener(new MouseAction());

	    mainFrame.add(drawPanel);
		mainFrame.setVisible(true);
	}
	
	public void new_game()
	{
		// set up board for a new game
		
		board = new Board(boardSize, difficulty);
		win = false;
		lose = false;
		
		// determine if there are any zero tiles on the board
		boolean hasZero = false;
		for(int y = 0; y < boardSize; y++)
		{
			for(int x = 0; x < boardSize; x++)
			{
				hasZero = hasZero || board.get_tile(x, y).get_value() == 0;
			}
		}	
		
		// place hint marker on a random zero tile
		if(hasZero)
		{
			int randX;
			int randY;
			Tile randTile;
			do
			{
				randX = (int) (Math.random() * boardSize);
				randY = (int) (Math.random() * boardSize);
				randTile = board.get_tile(randX, randY); 
			}
			while(randTile.get_value() != 0);
			hintTile = new int[] {randX, randY};
		}
	}
	
	class MouseAction extends MouseAdapter
	{
		@Override
	    public void mouseClicked(MouseEvent event)
	    {
			// handle mouse click event
			
			if(!lose && !win)
			{
				// convert pixel x,y coordinates to board x,y, coordinates
				int x = (event.getX() - padding + 1) / scale;
				int y = (event.getY() - padding + 1) / scale;
				if(x < 0 || x >= boardSize)
				{
					x = -1;
				}
				if(y < 0 || y >= boardSize)
				{
					y = -1;
				}
				
				// uncover a tile on left click if not flagged
				if(event.getButton() == MouseEvent.BUTTON1)
				{
					if(x >= 0 && y >= 0 && !board.is_flagged(x, y))
					{
						board.uncover_tile(x, y);
						if(board.get_tile(x, y).get_value() == -1)
						{
							lose = true;
						}
					}
				}
				// flag or unflag a tile on right click
				else if(event.getButton() == MouseEvent.BUTTON3)
				{
					if(x >= 0 && y >= 0)
					{
						boolean isFlag = board.is_flagged(x, y);
						board.set_flag(x, y, !isFlag);
					}
				}
	    	}

			// reset game when reset button is left clicked
			if(event.getButton() == MouseEvent.BUTTON1)
			{
				if(event.getX() > padding - 2 && event.getX() < padding - 2 + scale * 3 && event.getY() > boardSize * scale + padding * 2 + 1 && event.getY() < boardSize * scale + padding * 2 + 1 + scale + 2 )
				{
					new_game();
				}
			}

			// check for win condition
			if(board.is_complete())
			{
				win = true;
			}

			// redraw screen
			drawPanel.repaint();
		}
	}
		
	class GamePanel extends JPanel
	{
		
		// init color constants
		private Color[] colors = {
				new Color(68, 204, 0),
				new Color(204, 204, 0),
				new Color(204, 130, 0),
				new Color(204, 68, 0),
				new Color(204, 0, 0),
				new Color(204, 0, 68),
				new Color(204, 0, 170),
				new Color(136, 0, 204)
		};
		private Color bgColor = new Color(180, 210, 255);
		private Color boardColor = new Color(120, 120, 120);
		private Color tileColor1 = new Color(170, 170, 170);
		private Color tileColor2 = new Color(210, 210, 210);
		private Color mineColor = new Color(0, 0, 0);
		private Color flagColor = new Color(250, 10, 0);
		
	    @Override
	    public void paintComponent(Graphics g)
	    {
	    	// handle graphics
	    	
	    	Graphics2D g2 = (Graphics2D) g;

	    	// set font
	    	g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
     	    Font font = new Font("Roboto", Font.BOLD, scale);
     	    g.setFont(font);
     	    
     	    // draw background color
     	    g.setColor(bgColor);
     	    g.fillRect(0, 0, screenW, screenH);
     	    
     	    // draw board
     	    g.setColor(boardColor);
     	    g.fillRect(padding - 2, padding - 2, boardSize * scale + 1, boardSize * scale + 1);
	    	
     	    // draw reset button
     	    g.fillRect(padding - 2, boardSize * scale + padding * 2 + 1, scale * 3, scale + 2);
     	    g.setColor(mineColor);
     	    g.drawString("Reset", padding + 2, (boardSize + 1) * scale + padding * 2 - 1);
     	    
			// draw each tile
	    	for(int y = 0; y < boardSize; y++)
	    	{
	    		for(int x = 0; x < boardSize; x++)
	    		{
	    			Tile tileHere = board.get_tile(x, y);
	    			
	    			g.setColor(tileColor1);
	    			if(tileHere.is_uncovered())
	    			{
	    				g.setColor(tileColor2);
	    			}
	    			
	    			g.fillRect(x * scale + padding - 1, y * scale + padding - 1, scale - 1, scale - 1);
	    			
	    			// determine what to draw based on tile values -- value, mine, flag, or hint
	    			int gx = (int) (x * scale + padding * 1.4);
	    			int gy = (int) (y * scale + padding + scale * 0.75 + 2);
	    			if(tileHere.get_value() > 0 && tileHere.is_uncovered())
	    			{
	    				g.setColor(colors[tileHere.get_value() - 1]);
	    				g.drawString(Integer.toString(tileHere.get_value()), gx, gy);
	    			}
	    			else if(tileHere.get_value() == -1 && tileHere.is_uncovered())
	    			{
	    				g.setColor(mineColor);
	    				g.drawString("X", gx, gy);
	    			}
	    			else if(tileHere.is_flagged() && !tileHere.is_uncovered())
	    			{
	    				g.setColor(flagColor);
	    				g.drawString("F", gx, gy);
	    			}
	    			else if(x == hintTile[0] && y == hintTile[1] && !tileHere.is_uncovered())
	    			{
	    				g.setColor(flagColor);
	    				g.drawString("+", gx, gy);
	    			}
	    		}
	    	}
			
	    	// display messages for winning and losing
			if(win)
			{
				g.setColor(mineColor);
				g.drawString("You are winner!", scale * 6, (boardSize + 1) * scale + padding * 2 - 1);
			}
			if(lose)
			{
				g.setColor(mineColor);
				g.drawString("You suck.", scale * 8, (boardSize + 1) * scale + padding * 2 - 1);
			}
	    }
	}
}
