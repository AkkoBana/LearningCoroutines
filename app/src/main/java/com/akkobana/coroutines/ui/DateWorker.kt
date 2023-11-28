package com.akkobana.coroutines.ui

import android.icu.text.SimpleDateFormat
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date

class DateWorker(
    inputFolderPath: String,
    outputFolderPath: String,
    logFilePath: String
) {

    private val inputFolder = File(inputFolderPath)
    private val outputFolder = File(outputFolderPath)
    private val logFile = File(logFilePath)
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd")
    private var fileMap = mutableMapOf<String, MutableList<File>>()

    fun createLogFile() {
        if (!logFile.exists()) {
            logFile.createNewFile()
        }
    }

    fun getFileDates() {
        try {
            val files = inputFolder.listFiles()
            files?.forEach { file ->
                if (file.isFile) {
                    val fileName = file.nameWithoutExtension
                    val dateFromFileName = extractDateFromFileName(fileName)
                    val formattedDate = dateFromFileName?.toString() ?: dateFormat.format(Date(file.lastModified()))
                    val fileList = fileMap.getOrPut(formattedDate) { mutableListOf() }
                    fileList.add(file)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun renameFiles() {
        try {
            fileMap.forEach { (date, fileList) ->
                val folderName = "$date/"
                val folder = File(outputFolder, folderName)

                if (!folder.exists()) {
                    folder.mkdirs()
                }

                fileList.forEachIndexed { index, file ->
                    val extension = getFileExtension(file.name)
                    val newFileName = "$date (${(index + 1).toString().padStart(3, '0')})$extension"
                    val newFile = File(folder, newFileName)

                    file.renameTo(newFile)

                    val logMessage = "Переименован файл ${file.name} в ${newFile.name}\n"
                    logFile.appendText(logMessage)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun extractDateFromFileName(fileName: String): Date? {
        val formats = arrayOf("yyyyMMdd","yyyy-MM-dd")
        var date :Date? = null

        for (format in formats) {
            val sdf = SimpleDateFormat(format)
            sdf.isLenient = false
            date = sdf.parse(fileName)
            if(date != null) {
                break
            }
        }

        return date /*
        val datePatterns = arrayOf(
            """(\d{4}[-_]\d{2}[-_]\d{2})""",
            """(\\d{4})(\\d{2})(\\d{2})"""
        )
        datePatterns.forEach { pattern ->
            val regex = pattern.toRegex()
            regex.find(fileName)?.let {
                return it.value
            }
        }
        return ""*/
    }

    private fun getFileExtension(fileName: String): String {
        val lastDotIndex = fileName.lastIndexOf('.')
        return if (lastDotIndex != -1) {
            fileName.substring(lastDotIndex)
        } else {
            ""
        }
    }
}