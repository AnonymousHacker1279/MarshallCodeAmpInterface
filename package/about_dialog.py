from PySide6.QtWidgets import QDialog

from ui.about_dialog_ui import Ui_Dialog


class AboutDialog(QDialog, Ui_Dialog):
	def __init__(self):
		super().__init__()
		self.setupUi(self)
