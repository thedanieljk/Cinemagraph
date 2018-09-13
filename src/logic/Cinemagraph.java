package logic;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
//import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import com.sun.imageio.plugins.gif.GIFImageReader;
import com.sun.imageio.plugins.gif.GIFImageReaderSpi;

public class Cinemagraph {
	/*
	 * The Function takes in an existing JFrame and opens a gif in the same folder as the project
	 */

	public void playGif(JFrame f) throws MalformedURLException {
		URL url = getClass().getResource("treewater.gif");
		ImageIcon icon = new ImageIcon(url);
	    JLabel label = new JLabel(icon);
		
	    //JFrame f = new JFrame("Cinemagraph");
	    f.getContentPane().add(label);
	    f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    f.pack();
	    f.setLocationRelativeTo(null);
        f.setVisible(true);
	}
	
	
	/* 
	 * Opens the gif as an ArrayList of BufferedImages, allowing me to look at individual frames 
	 * This is done in order to pull height, width, and RGB data from the gif
	 */
	
	public ArrayList<BufferedImage> getImages(File gif) throws IOException {
		ArrayList<BufferedImage> imgs = new ArrayList<BufferedImage>();
		ImageReader rdr = new GIFImageReader(new GIFImageReaderSpi());
		rdr.setInput(ImageIO.createImageInputStream(gif));
		for (int i=0;i < rdr.getNumImages(true); i++) {
			imgs.add(rdr.read(i));
		}
		return imgs;
	}
	
	/*
	 * Creates a transparent buffered image that is overlayed on the gif
	 * This is done to allow the highlighting of the gif (through changing the transparency of this transparent image
	 * in areas in which the user has selected
	 */
	
	// Make a separate function to make an individual pixel transparent?
	public BufferedImage getTransImg(int width, int height) {
		BufferedImage transImg = new BufferedImage(width,height, BufferedImage.TYPE_INT_ARGB);
		int transparency = 0;
		int colorMask = 0x00FFFFFF;
		int alphaShift = 24;
		
		for (int y = 0; y < height ; y++) {
			for (int x = 0; x < width ; x++) {
				//transImg.getRGB(x, y) to get RGB, rn using (255,0,0) to be red
				transImg.setRGB(x, y, (0xFF0000 & colorMask) | (transparency << alphaShift));
			}
		}
		
		return transImg;
	}
	
	/*
	 * Plays simple Greeting. (Usage instructions?)
	 */
	
	public void playGreeting() {
		System.out.println("Welcome to Daniel's Cinemagraph maker.");
		System.out.println("This will only work for optimized gifs.");
		
	}
	
	/*
	 * Play Instructions for using the Cinemagraph Maker
	 */
	
	public void playInstructions() {
		System.out.println("\nYou are now ready to select an area.");
		System.out.println("Please select an area to remain in movement");
		System.out.println("Click and drag with the mouse to select each rectangular area");
		System.out.println("Press SPACE when you are done selecting an area");
	}
	
	
	/*
	 * Main function to run the project
	 */
	
	public void printCoords(int sX, int sY, int eX, int eY) {
		//System.out.println("Starting at " + sX + "," + sY);
		//System.out.println("Ending at " + eX + "," + eY);
		
	}
	
	public boolean[][] fillMask(boolean arr[][]) {
		//System.out.println("arr length is" + arr.length);
		//System.out.println("arr[] length is" + arr[0].length);
		for (int i = 0; i < arr.length; i++) {
			for (int j = 0;j < arr[0].length; j++) {
				arr[i][j] = false;
			}
		}
		return arr;
	}
	
	public void updateMask(Mask m, int x1, int y1, int x2, int y2) {
		
		//System.out.println("mask arr size = " + m.mask.length + "," + m.mask[0].length);
		
		int minX, maxX, minY, maxY = 0;
		if (x1 < x2) {
			minX = x1;
			maxX = x2;
		}
		else {
			minX = x2;
			maxX = x1;
		}
		
		if (y1 < y2) {
			minY = y1;
			maxY = y2;
		}
		else {
			minY = y2;
			maxY = y1;
		}
		System.out.println("A rectangle from ("+ minX + "," + minY + ") to (" + maxX + "," + maxY + ") has been added to the mask");
		for (int i = minX; i <= maxX; i++) {
			for (int j = minY; j <= maxY; j++) {
				m.mask[i][j] = true;
			}
		}

	}
	
	public void updatePixel(Mask m, int x, int y) {
		int rgb = m.imgs.get(0).getRGB(x, y);
		for (int i = 0; i < m.imgs.size() ; i++ ) {
			//System.out.println("Updating at " + x + "," + y);
			try {
				m.imgs.get(i).setRGB(x,y,rgb);
			} catch (Exception e) {
				//System.out.println( e.getClass().getCanonicalName() + " at index " + i);
			}
		}
	}
	//getRGB, setRGB
	//first = imgs.get(0)
	//first.getRGB?
	//loop thru entire image
	//imgs.get(i).setRGB(
	public void editImage(Mask m) {
		//BufferedImage first = m.imgs.get(0);
		
		for (int i = 0; i < m.mask.length; i++) {
			for (int j = 0; j < m.mask[0].length; j++) {
				if (m.mask[i][j] == false) {
					//System.out.println("Don't change coords " + i + "," + j);
					updatePixel(m,i,j);
				}
			}
		}
	}
	
	public void convertGif(Mask m) throws IIOException, IOException {
		BufferedImage firstImage = m.imgs.get(0);
		ImageOutputStream output = new FileImageOutputStream(new File("output.gif"));
		GifSequenceWriter writer = new GifSequenceWriter(output, BufferedImage.TYPE_INT_ARGB, 1, true);
		
		writer.writeToSequence(firstImage);
		for (int i = 1; i < m.imgs.size();i++) {
			BufferedImage nextImage = m.imgs.get(i);
			writer.writeToSequence(nextImage);
		}
		System.out.println("The file has been written");
		writer.close();
		output.close();
	}
	
	@SuppressWarnings("serial")
	public static void main(String[] args) throws MalformedURLException, IOException, IIOException {
		Cinemagraph cin = new Cinemagraph();
		cin.playGreeting();
		
		//Get gif as ArrayList of BufferedImages, create JFrame using this data
		URL path = Cinemagraph.class.getResource("treewater.gif");
		ArrayList<BufferedImage> imgs = cin.getImages(new File(path.getFile()));
		//System.out.println("imgs size is " + imgs.size());
		int width = imgs.get(0).getWidth();
		int height = imgs.get(0).getHeight();
		boolean mask[][] = new boolean[width][height];
		mask = cin.fillMask(mask);
		Mask m = new Mask(mask,imgs);

		JFrame f = new JFrame("Cinemagraph");
			
		//Make a transparent image that is on top of the gif	
		BufferedImage transImg = cin.getTransImg(width, height);
		f.getContentPane().add(new JLabel(new ImageIcon(transImg)));
		f.pack();
		f.setVisible(true);
		//should be transparent layer now
		
		cin.playGif(f); //play the gif (hardcoded file)
		
		//Play instructions once the gif has been displayed
		cin.playInstructions();
		
		//Allow user to select the area
		
		JPanel p = new JPanel() {
	        Point pointStart = null;
	        Point pointEnd   = null;
	        Cinemagraph c = new Cinemagraph();
	        {
	            addMouseListener(new MouseAdapter() {
	                public void mousePressed(MouseEvent e) {
	                    pointStart = e.getPoint();
	                }

	                public void mouseReleased(MouseEvent e) {
	                    pointEnd = e.getPoint();
	                    c.printCoords(pointStart.x, pointStart.y, pointEnd.x, pointEnd.y);
	                    c.updateMask(m,pointStart.x, pointStart.y, pointEnd.x, pointEnd.y);
	                }
	            });
	        }
	    };
	    f.add(p);
	    f.setVisible(true);
		//
		/*Set up KeyBindings for Space, which should trigger ActionEvent to begin the process of adjusting pixels*/
		KeyStroke space = KeyStroke.getKeyStroke(KeyEvent.VK_SPACE,0,false);
		Action spaceAction = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				Cinemagraph c = new Cinemagraph();
				System.out.println("\nSpace has been pressed, editing image now");
				c.editImage(m);
				try {
					c.convertGif(m);
				} catch (Exception ex) {
					System.out.println("error");
				}
			}
		};
		
		f.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(space, "SPACE");
		f.getRootPane().getActionMap().put("SPACE", spaceAction);;
		
	}
}
