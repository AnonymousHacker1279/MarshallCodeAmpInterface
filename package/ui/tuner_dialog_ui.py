# -*- coding: utf-8 -*-

################################################################################
## Form generated from reading UI file 'tuner_dialog.ui'
##
## Created by: Qt User Interface Compiler version 6.7.2
##
## WARNING! All changes made in this file will be lost when recompiling UI file!
################################################################################

from PySide6.QtCore import (QCoreApplication, QDate, QDateTime, QLocale,
    QMetaObject, QObject, QPoint, QRect,
    QSize, QTime, QUrl, Qt)
from PySide6.QtGui import (QBrush, QColor, QConicalGradient, QCursor,
    QFont, QFontDatabase, QGradient, QIcon,
    QImage, QKeySequence, QLinearGradient, QPainter,
    QPalette, QPixmap, QRadialGradient, QTransform)
from PySide6.QtWidgets import (QApplication, QDialog, QGraphicsView, QLabel,
    QSizePolicy, QWidget)
import package.ui.resources_rc

class Ui_Dialog(object):
    def setupUi(self, Dialog):
        if not Dialog.objectName():
            Dialog.setObjectName(u"Dialog")
        Dialog.resize(500, 400)
        icon = QIcon()
        icon.addFile(u":/code50amp/resources/code50.png", QSize(), QIcon.Mode.Normal, QIcon.State.On)
        Dialog.setWindowIcon(icon)
        Dialog.setModal(True)
        self.tunerLabel = QLabel(Dialog)
        self.tunerLabel.setObjectName(u"tunerLabel")
        self.tunerLabel.setGeometry(QRect(210, 30, 90, 70))
        font = QFont()
        font.setPointSize(28)
        font.setBold(True)
        font.setItalic(True)
        self.tunerLabel.setFont(font)
        self.tunerLabel.setAlignment(Qt.AlignmentFlag.AlignCenter)
        self.tunerGraphicsView = QGraphicsView(Dialog)
        self.tunerGraphicsView.setObjectName(u"tunerGraphicsView")
        self.tunerGraphicsView.setGeometry(QRect(0, 125, 500, 250))

        self.retranslateUi(Dialog)

        QMetaObject.connectSlotsByName(Dialog)
    # setupUi

    def retranslateUi(self, Dialog):
        Dialog.setWindowTitle(QCoreApplication.translate("Dialog", u"Tuner", None))
        self.tunerLabel.setText(QCoreApplication.translate("Dialog", u"E", None))
    # retranslateUi

