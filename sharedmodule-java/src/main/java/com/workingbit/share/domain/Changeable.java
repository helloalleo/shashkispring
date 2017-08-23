package com.workingbit.share.domain;


import com.workingbit.share.domain.impl.BoardContainer;

public interface Changeable {
	/**
	 * Undoes an action
	 */
	BoardContainer undo();

	/**
	 * Redoes an action
	 */
	BoardContainer redo();
}