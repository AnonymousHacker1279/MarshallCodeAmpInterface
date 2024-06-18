import sys

from PySide6.QtWidgets import QApplication

from app import AmpInterfaceWindow

if __name__ == "__main__":
	app = QApplication(sys.argv)
	window = AmpInterfaceWindow()
	window.show()
	sys.exit(app.exec())
