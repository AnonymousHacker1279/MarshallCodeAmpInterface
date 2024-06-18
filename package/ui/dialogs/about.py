# -*- coding: utf-8 -*-

################################################################################
## Form generated from reading UI file 'about_dialog.ui'
##
## Created by: Qt User Interface Compiler version 6.7.1
##
## WARNING! All changes made in this file will be lost when recompiling UI file!
################################################################################

from PySide6.QtCore import (QCoreApplication, QMetaObject, QRect,
                            QSize)
from PySide6.QtGui import (QIcon)
from PySide6.QtWidgets import (QLabel)


class Ui_Dialog(object):
    def setupUi(self, Dialog):
        if not Dialog.objectName():
            Dialog.setObjectName(u"Dialog")
        Dialog.resize(600, 400)
        icon = QIcon()
        icon.addFile(u":/code50amp/resources/code50.png", QSize(), QIcon.Normal, QIcon.On)
        Dialog.setWindowIcon(icon)
        Dialog.setSizeGripEnabled(False)
        Dialog.setModal(True)
        self.label = QLabel(Dialog)
        self.label.setObjectName(u"label")
        self.label.setGeometry(QRect(10, 10, 581, 381))

        self.retranslateUi(Dialog)

        QMetaObject.connectSlotsByName(Dialog)
    # setupUi

    def retranslateUi(self, Dialog):
        Dialog.setWindowTitle(QCoreApplication.translate("Dialog", u"About", None))
        self.label.setText(QCoreApplication.translate("Dialog", u"<html><head/><body><p align=\"center\"><span style=\" font-size:12pt; font-style:italic;\">Marshall CODE Interface by </span><a href=\"https://anonymoushacker1279.tech\"><span style=\" font-size:12pt; font-style:italic; text-decoration: underline; color:#ffffff;\">AnonymousHacker1279</span></a></p><p align=\"center\"><span style=\" font-size:10pt;\">A desktop program to modify amp settings without needing to use the Marshall Gateway app.</span></p><p align=\"center\"><img src=\":/code50amp/resources/code50.png\"/><br/></p><p align=\"center\">Marshall and Marshall CODE are registered trademarks of Marshall Amplification PLC.</p><p align=\"center\">Images are property of Marshall Amplification PLC.</p></body></html>", None))
    # retranslateUi

