/**
 * *****************************************************************************
 * Copyright (c) 2009 David Orme and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Orme - initial API and implementation
 * *****************************************************************************
 */
package org.ozb.xscalawt

import org.eclipse.swt.widgets._
import org.eclipse.swt.layout._
import org.eclipse.jface.layout._

/**
 * XScalaWTSetups is a utility object containing functions making it easier for
 * clients to extend XScalaWT.
 */
object XScalaWTAPI {
	// Start with any Widget and add children from there...

	class WidgetX[W](w: W) {
		def apply(setups: (W => Any)*): W = {
			//println("apply(setups)")
			setupAndReturn(w, setups: _*)
		}
		def contains(setups: (W => Any)*): W = {
			setupAndReturn(w, setups: _*)
		}
		
		def setID(id: Symbol) {
			//println("  setID " + id)
			w match {
				case w: Widget => w.setData("WIDGET_ID", id)
				case _ => sys.error("can only set ID on widgets !")
			}
		}
	}

	/**
	 * def setup[T].  Set up a new control and call all of its setup functions.
	 *
	 * T is the control class
	 * @param control the SWT Control
	 * @param setups A vararg list of setup functions of the type (T => Any)
	 * @return T the T that was set up
	 */
	def setupAndReturn[T](control: T, setups: (T => Any)*): T = {
		//println("setupAndReturn control " + control)
		setups.foreach(setup => setup(control))

		if (control.isInstanceOf[Composite]) {
			val composite = control.asInstanceOf[Composite]
			if (composite.getLayout().isInstanceOf[GridLayout]) {
				composite.getChildren.foreach { child =>
					if (child.getLayoutData == null) {
						GridDataFactory.defaultsFor(child).applyTo(child)
					}
				}
			}
		}
		control
	}

	def ignore[T]: T => Unit = (t: T) => {}
}
