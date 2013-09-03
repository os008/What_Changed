What Changed v2.03.0050
=======================

A program that monitors file changes and reports them either in real-time or manually.


Features:
•	Select files to be kept track of.
•	Save file-state in a database when snapshotted.
•	Generate a report for the current file-state.
•	Generate a report of the difference between the last state and the current state.
•	Show a summary of changed files.
•	Show a detailed report of all files selected.
•	Monitor files in real-time and pop a notification when something happens.


Instructions:
++  Startup:
•	After the file-dialogue appears, choose all the hard-drive drives in your system.
•	The drives selection can be changed later using the “change drives” button.
•	The folder/files tree can be refreshed using the 'refresh' button.
++  Choosing files:
•	The files in each folder are collected under the header “[Files]”.
•	The system only monitor files, so choosing folders is un-necessary.
•	Choosing files adds them to the list on the right.
•	More than one file can be selected in the right pane for removal. The tick can be toggled using “space” to tick, and “enter” to un-tick.
++  Generating reports:
•	Before generating a report, the “snapshot” button must be pressed to capture the current state of files first.
•	Pressing the “snapshot” button creates a new checkpoint for the files.
•	Pressing the “generate report” button creates and shows a summary report.
•	The summary report shows the names of files, from the list of files selected, that were changed.
•	Pressing on the “show full-report” opens-up a window that shows the full list of files, with their associated changes.
•	“--“ indicates the file is missing in its respective state.
•	The left panel is the old state, while the right is the new state.
--->>>>> The program can’t be exited without taking a “snapshot” of the files in the list (if the list changes). <<<<<---
++  Real-time monitoring:
•	Pressing the “monitor” button allows the program to enter a real-time monitoring state.
•	In this state, the program is minimised to the system-tray (if supported by the OS).
•	It also pops-up notifications near the lower-right corner of the screen indicating the kind of change (nearly instantly).


Copyright (C) 2013 by Ahmed Osama el-Sawalhy
 *		Modified MIT License (GPL v3 compatible).
