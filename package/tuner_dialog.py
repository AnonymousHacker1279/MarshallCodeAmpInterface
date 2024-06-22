from PySide6.QtCore import QRectF
from PySide6.QtGui import QPixmap, QBrush, QPen, Qt
from PySide6.QtWidgets import QDialog, QGraphicsScene, QGraphicsRectItem

from ui.tuner_dialog_ui import Ui_Dialog


class TunerDialog(QDialog, Ui_Dialog):
	def __init__(self, main):
		super().__init__()
		self.setupUi(self)
		self.main = main
		self.scene = QGraphicsScene()
		self.tuning_rectangle = QGraphicsRectItem()
		self.__setup_graphics_scene()

	def closeEvent(self, event) -> None:
		self.main.interface.set_tuner_state(False)
		event.accept()

	def __setup_graphics_scene(self) -> None:
		"""Set up the graphics scene to display the tuning accuracy tool."""
		pixmap = QPixmap(":/Tuner_Background/resources/tuner_background.png")
		pixmap_item = self.scene.addPixmap(pixmap)
		pixmap_item.setPos(0, 0)

		# Set the scene's rectangle to the size of the pixmap
		self.scene.setSceneRect(QRectF(pixmap.rect()))

		self.tunerGraphicsView.setScene(self.scene)

	def draw_tuner(self, note: str, accuracy: int) -> None:
		"""Draw the tuner on the screen."""
		self.tunerLabel.setText(note)
		self.scene.removeItem(self.tuning_rectangle)

		box_data = [
			(12, 96, Qt.GlobalColor.red),
			(122, 76, Qt.GlobalColor.yellow),
			(212, 56, Qt.GlobalColor.green),
			(282, 76, Qt.GlobalColor.yellow),
			(372, 96, Qt.GlobalColor.red)
		]

		x, width, color = box_data[accuracy]

		# Draw a rectangle to represent the accuracy of the tuning
		self.tuning_rectangle = QGraphicsRectItem(x, 102, width, 36)
		self.tuning_rectangle.setBrush(QBrush(color))
		self.tuning_rectangle.setPen(QPen(Qt.GlobalColor.black))
		self.scene.addItem(self.tuning_rectangle)
