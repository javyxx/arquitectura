package com.paradigma.arquitecture.command;

/**
 * The Class AbsctracCommand.
 *
 * @author Javier Ledo Vázquez
 * @version 1.0
 */
public abstract class AbstractCommand implements Runnable {

	@Override
	public void run() {
		execute();
	}

	protected abstract void execute();

}
