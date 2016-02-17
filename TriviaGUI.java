package il.ac.tau.cs.sw1.trivia;

import java.awt.Event;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class TriviaGUI {

	private static final int MAX_ERRORS = 3;
	private Shell shell;
	private Label scoreLabel;
	private Composite questionPanel;
	private Font boldFont;
	private String lastAnswer = "";
	
	private int countques;
	private int countErr;
	private boolean[] wasAsked;
	private Random rand;
	public String rightAns;
	private int score;
	private List<String[]> allques;
	private List<String> ans;

	


	public void open() {
		createShell();
		runApplication();
	}

	/**
	 * Creates the widgets of the application main window
	 */
	private void createShell() {
		Display display = Display.getDefault();
		shell = new Shell(display);
		shell.setText("Trivia");

		// window style
		Rectangle monitor_bounds = shell.getMonitor().getBounds();
		shell.setSize(new Point(monitor_bounds.width / 3,
				monitor_bounds.height / 4));
		shell.setLayout(new GridLayout());

		FontData fontData = new FontData();
		fontData.setStyle(SWT.BOLD);
		boldFont = new Font(shell.getDisplay(), fontData);
		
		//create window panels
		createFileLoadingPanel();
		createScorePanel();
		createQuestionPanel();
	}

	/**
	 * Creates the widgets of the form for trivia file selection
	 */
	private void createFileLoadingPanel() {
		final Composite fileSelection = new Composite(shell, SWT.NULL);
		fileSelection.setLayoutData(GUIUtils.createFillGridData(1));
		fileSelection.setLayout(new GridLayout(4, false));

		final Label label = new Label(fileSelection, SWT.NONE);
		label.setText("Enter trivia file path: ");

		// text field to enter the file path
		final Text filePathField = new Text(fileSelection, SWT.SINGLE
				| SWT.BORDER);
		filePathField.setLayoutData(GUIUtils.createFillGridData(1));

		// "Browse" button 
		final Button browseButton = new Button(fileSelection,
				SWT.PUSH);
		browseButton.setText("Browse");

		// "Play!" button
		final Button playButton = new Button(fileSelection, SWT.PUSH);
		playButton.setText("Play!");
		
		browseButton.addSelectionListener(new SelectionListener() {

		      public void widgetSelected(SelectionEvent arg0) {
		    	 String path =GUIUtils.getFilePathFromFileDialog(shell);
		    	 if (path!=null){
		    		 filePathField.setText(path);
		    	 }
		      }

		      public void widgetDefaultSelected(SelectionEvent arg0) {
		      }
		    });
		
		playButton.addSelectionListener(new SelectionListener() {

		      public void widgetSelected(SelectionEvent arg0) {
		    	  allques = null;
		    	  if(filePathField.getText()!=null){
		    		  try {
		    			  
		    			  
		    			  allques=new ArrayList<String[]>();
		    			  
		    			String CurrentLine;
		    		  
						BufferedReader br = new BufferedReader(new FileReader(filePathField.getText()));
						while ((CurrentLine = br.readLine()) != null) {
							String[] ans=CurrentLine.split("\t");
							
							allques.add(ans);
						}
						br.close();
					} 
		    		  
		    		  catch (IOException e) {

		    			  GUIUtils.showErrorDialog(shell,"problem with the file !!");
					}
		    		  
		    		  
		    	  }
		    	  
		    	  else{
		    		  GUIUtils.showErrorDialog(shell,"select file !!");
		    	  }
		    	  
		    	  
		    	  rand=new Random();
		    	  wasAsked=new boolean[allques.size()];
		    	  score=0;
		    	  makeNewQuestion();

		      }

		      public void widgetDefaultSelected(SelectionEvent arg0) {
		      }
		    });
	}
	

	
	/**
	 * Creates the panel that displays the current score
	 */
	private void createScorePanel() {
		Composite scorePanel = new Composite(shell, SWT.BORDER);
		scorePanel.setLayoutData(GUIUtils.createFillGridData(1));
		scorePanel.setLayout(new GridLayout(2, false));

		final Label label = new Label(scorePanel, SWT.NONE);
		label.setText("Total score: ");

		// The label which displays the score; initially empty
		scoreLabel = new Label(scorePanel, SWT.NONE);
		scoreLabel.setLayoutData(GUIUtils.createFillGridData(1));
	}
	
	private void updateScorePanel(int score) {

		scoreLabel.setText(String.valueOf(this.score));
	}
	

	/**
	 * Creates the panel that displays the questions, as soon as the game starts.
	 * See the updateQuestionPanel for creating the question and answer buttons
	 */
	private void createQuestionPanel() {
		questionPanel = new Composite(shell, SWT.BORDER);
		questionPanel.setLayoutData(new GridData(GridData.FILL,
				GridData.FILL, true, true));
		questionPanel.setLayout(new GridLayout(2, true));

		//Initially, only displays a message
		Label label = new Label(questionPanel, SWT.NONE);
		label.setText("No question to display, yet.");
		label.setLayoutData(GUIUtils.createFillGridData(2));
	}

	public void makeNewQuestion(){
		
		//System.out.println(this.countErr +"\\" +countques+" "+ allques.size() );
		 updateScorePanel(score);
		if ((countques>=allques.size()) || (countErr==3)){
			
			String msg="Your final score is "+this.score+" after "+this.countques+" questios";
			GUIUtils.showInfoDialog(shell, "GAME OVER", msg);
		}
		
		else
		{

				int randques=rand.nextInt(allques.size());
		    	while(wasAsked[randques]){
		    		  randques=rand.nextInt(allques.size());
		    	  }
		    	wasAsked[randques]=true;
		    	
		    	 ans=new ArrayList<String>();
		   	  String right=allques.get(randques)[1];
		   	  ans.add(right);
		   	  ans.add(allques.get(randques)[2]);
		   	  ans.add(allques.get(randques)[3]);
		   	  ans.add(allques.get(randques)[4]);
		   	  Collections.shuffle(ans);
		   	  rightAns=right;
		   	  
		   	  updateQuestionPanel(allques.get(randques)[0],ans);
		    	
		   	  this.countques++;
		}
		
	}
	/**
	 * Serves to display the question and answer buttons
	 */
	private void updateQuestionPanel(String question,
			List<String> answers) {
		
		// clear the question panel
		Control[] children = questionPanel.getChildren();
		for (Control control : children) {
			control.dispose();
		}

		// create the instruction label
		Label instructionLabel = new Label(questionPanel, SWT.CENTER
				| SWT.WRAP);
		instructionLabel.setText(lastAnswer 
				+ "Answer the following question:");
		instructionLabel
				.setLayoutData(GUIUtils.createFillGridData(2));

		// create the question label
		Label questionLabel = new Label(questionPanel, SWT.CENTER
				| SWT.WRAP);
		questionLabel.setText(question);
		questionLabel.setFont(boldFont);
		questionLabel.setLayoutData(GUIUtils.createFillGridData(2));

		// create the answer buttons
		for (int i = 0; i < 4; i++) {
			Button answerButton = new Button(questionPanel, SWT.PUSH
					| SWT.WRAP);
			answerButton.setText(answers.get(i));
			GridData answerLayoutData = GUIUtils
					.createFillGridData(1);
			answerLayoutData.verticalAlignment = SWT.FILL;
			answerButton.setLayoutData(answerLayoutData);
			answerButton.addSelectionListener(new SelectionListener() {
				
			      public void widgetSelected(SelectionEvent arg0) {
			    	  
			    	  Button a=(Button) arg0.getSource();	
			    	  String thisans=a.getText();
			    	  if(thisans.equals(rightAns)){
			    		  score+=3;
			    	  }
			    	  else{
			    		  score-=1;
			    		  countErr++;
			    		  
			    	  }
			    	 
			    	  makeNewQuestion();
			      }

				@Override
				public void widgetDefaultSelected(SelectionEvent e) {

					
				}
			});
			
			
		}

		// create the "Pass" button to skip a question
		Button passButton = new Button(questionPanel, SWT.PUSH);
		passButton.setText("Pass");
		GridData data = new GridData(GridData.CENTER,
				GridData.CENTER, true, false);
		data.horizontalSpan = 2;
		passButton.setLayoutData(data);
		passButton.addSelectionListener(new SelectionListener() {

		      public void widgetSelected(SelectionEvent arg0) {
		    	  makeNewQuestion();
		      }

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

				
			}
		});

		// two operations to make the new widgets display properly
		questionPanel.pack();
		questionPanel.getParent().layout();
	}

	/**
	 * Opens the main window and executes the event loop of the application
	 */
	private void runApplication() {
		shell.open();
		Display display = shell.getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
		boldFont.dispose();
	}
}
