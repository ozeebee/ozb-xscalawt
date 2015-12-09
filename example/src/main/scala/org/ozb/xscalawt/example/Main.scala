package org.ozb.xscalawt.example

import org.ozb.xscalawt.XScalaWT.Assignments._
import org.ozb.xscalawt.XScalaWT._
import org.ozb.xscalawt.XScalaWTBinding._

import org.eclipse.swt.widgets._
import org.eclipse.swt.SWT
import org.eclipse.swt.events.SelectionEvent

object Main {
	val appName = "xscalawt-example"
	var theDisplay: Display = _
	var theShell: Shell = _
	
	def main(args: Array[String]): Unit = {
		println("***** Starting *****")

		// Change system fonts (Mac OS X only) to use small fonts
		System.setProperty("org.eclipse.swt.internal.carbon.smallFonts", "")

		// let's get this GUI started
		startGUI()

		println("***** Finished *****")
		System.exit(0)
	}


	def startGUI(): Unit = {
		try {
			Display.setAppName(appName)
			theDisplay = new Display()
			runWithSWTRealm(theDisplay) {
				theShell = getWindow
				runEventLoop(theShell)
			}
		} finally {
			dispose()
		}
	}

	def dispose(): Unit = {
		// dispose your resources here (menus, toolbars, ...)
	}

	// ~~~~~~~~~~~~~~~~~ UI specific code ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	def getWindow: Shell = shell(
		label("Hello world !"),
		button("Say Hello", sayHello),
		setShellSizeAndPos
	)

	def sayHello(e: SelectionEvent): Unit = {
		val msgBox = new MessageBox(theShell, SWT.OK)
		msgBox.setText("A kind message")
		msgBox.setMessage("Hi there !")
		msgBox.open()
	}

	def setShellSizeAndPos(shell: Shell): Unit = {
		shell.setSize(400, 300)
		centerShell(shell)
	}

	def centerShell(shell: Shell): Unit = {
		val bounds = shell.getDisplay().getPrimaryMonitor().getBounds()
		val rect = shell.getBounds()
		val x = bounds.x + (bounds.width - rect.width) / 2
		val y = bounds.y + (bounds.height - rect.height) / 2
		shell.setLocation(x, y)
	}

}