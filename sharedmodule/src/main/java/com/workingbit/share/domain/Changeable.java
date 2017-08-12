package com.workingbit.share.domain;

import com.workingbit.share.domain.impl.BoardChanger;

public interface Changeable {
	/**
	 * Undoes an action
	 */
	BoardChanger undo();

	/**
	 * Redoes an action
	 */
	BoardChanger redo();
}