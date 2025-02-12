package fpozzi.utils.swing;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

public class UndoableTextField extends JTextField
{
	public final static String UNDO_ACTION = "Undo";

	public final static String REDO_ACTION = "Redo";
	
	public UndoableTextField(int i)
	{
		super(i);
		init();
	}
	
	public UndoableTextField()
	{
		init();
	}
	
	private void init()
	{
		final UndoManager undoMgr = new UndoManager();

		// Add listener for undoable events
		getDocument().addUndoableEditListener(new UndoableEditListener()
		{
			public void undoableEditHappened(UndoableEditEvent pEvt)
			{
				undoMgr.addEdit(pEvt.getEdit());
			}
		});

		// Add undo/redo actions
		getActionMap().put(UNDO_ACTION, new AbstractAction(UNDO_ACTION)
		{
			public void actionPerformed(ActionEvent pEvt)
			{
				try
				{
					if (undoMgr.canUndo())
					{
						undoMgr.undo();
					}
				} catch (CannotUndoException e)
				{
					e.printStackTrace();
				}
			}
		});
		getActionMap().put(REDO_ACTION, new AbstractAction(REDO_ACTION)
		{
			public void actionPerformed(ActionEvent pEvt)
			{
				try
				{
					if (undoMgr.canRedo())
					{
						undoMgr.redo();
					}
				} catch (CannotRedoException e)
				{
					e.printStackTrace();
				}
			}
		});

		// Create keyboard accelerators for undo/redo actions (Ctrl+Z/Ctrl+Y)
		getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK), UNDO_ACTION);
		getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_DOWN_MASK), REDO_ACTION);
	}


}