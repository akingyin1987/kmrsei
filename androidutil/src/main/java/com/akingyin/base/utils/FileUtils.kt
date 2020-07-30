package com.akingyin.base.utils

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.provider.MediaStore
import android.text.TextUtils
import androidx.annotation.RequiresApi
import com.akingyin.base.ext.md5
import java.io.*
import java.nio.ByteBuffer
import java.nio.channels.FileChannel
import java.util.*

/**
 * @ Description:
 *
 * 文件类操作
 * @ Author king
 * @ Date 2016/10/14 10:49
 * @ Version V1.0
 */
object FileUtils {
    const val FILE_EXTENSION_SEPARATOR = "."

    /**
     * read file
     *
     * @param charsetName The name of a supported [ &lt;/code&gt;charset&lt;code&gt;][java.nio.charset.Charset]
     * @return if file not exist, return null, else return content of file
     * @throws RuntimeException if an error occurs while operator BufferedReader
     */
    fun readFile(filePath: String, charsetName: String): StringBuilder? {
        val file = File(filePath)
        val fileContent = StringBuilder("")
        if (!file.isFile) {
            return null
        }
        var reader: BufferedReader? = null
        return try {
            val `is` = InputStreamReader(FileInputStream(file), charsetName)
            reader = BufferedReader(`is`)
            var line: String? = null
            while (reader.readLine().also { line = it } != null) {
                if ("" != fileContent.toString()) {
                    fileContent.append("\r\n")
                }
                fileContent.append(line)
            }
            fileContent
        } catch (e: IOException) {
            throw RuntimeException("IOException occurred. ", e)
        } finally {
            IOUtils.close(reader)
        }
    }
    /**
     * write file
     *
     * @param append is append, if true, write to the end of file, else clear content of file and
     * write into it
     * @return return false if content is empty, true otherwise
     * @throws RuntimeException if an error occurs while operator FileWriter
     */
    /**
     * write file, the string will be written to the begin of the file
     */
    @JvmOverloads
    fun writeFile(filePath: String, content: String?, append: Boolean = false): Boolean {
        return content?.let {
            makeDirs(filePath)
            try {
                return FileWriter(filePath, append).use { fileWriter ->
                    fileWriter.write(it)
                    true
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            false
        } ?: false

    }
    /**
     * write file
     *
     * @param append is append, if true, write to the end of file, else clear content of file and
     * write into it
     * @return return false if contentList is empty, true otherwise
     * @throws RuntimeException if an error occurs while operator FileWriter
     */
    /**
     * write file, the string list will be written to the begin of the file
     */
    @JvmOverloads
    fun writeFile(filePath: String, contentList: List<String>?, append: Boolean = false): Boolean {

        return when {
            contentList.isNullOrEmpty() -> {
                false
            }
            else -> {
                var fileWriter: FileWriter? = null
                return try {
                    makeDirs(filePath)
                    fileWriter = FileWriter(filePath, append)
                    var i = 0
                    for (line in contentList) {
                        if (i++ > 0) {
                            fileWriter.write("\r\n")
                        }
                        fileWriter.write(line)
                    }
                    true
                } catch (e: IOException) {
                    throw RuntimeException("IOException occurred. ", e)
                } finally {
                    IOUtils.close(fileWriter)
                }
            }
        }

    }
    /**
     * write file
     *
     * @param filePath the file to be opened for writing.
     * @param stream the input stream
     * @param append if `true`, then bytes will be written to the end of the file rather
     * than the beginning
     * @return return true
     * @throws RuntimeException if an error occurs while operator FileOutputStream
     */
    /**
     * write file, the bytes will be written to the begin of the file
     *
     * @see {@link .writeFile
     */
    @JvmOverloads
    fun writeFile(filePath: String?, stream: InputStream, append: Boolean = false): Boolean {
        return writeFile(filePath?.let { File(it) }, stream, append)
    }
    /**
     * write file
     *
     * @param file the file to be opened for writing.
     * @param stream the input stream
     * @param append if `true`, then bytes will be written to the end of the file rather
     * than the beginning
     * @return return true
     * @throws RuntimeException if an error occurs while operator FileOutputStream
     */
    /**
     * write file, the bytes will be written to the begin of the file
     *
     * @see {@link .writeFile
     */
    @JvmOverloads
    fun writeFile(file: File?, stream: InputStream, append: Boolean = false): Boolean {
        var o: OutputStream? = null
        return try {
            makeDirs(file!!.absolutePath)
            o = FileOutputStream(file, append)
            val data = ByteArray(1024)
            var length = -1
            while (stream.read(data).also { length = it } != -1) {
                o.write(data, 0, length)
            }
            o.flush()
            true
        } catch (e: FileNotFoundException) {
            throw RuntimeException("FileNotFoundException occurred. ", e)
        } catch (e: IOException) {
            throw RuntimeException("IOException occurred. ", e)
        } finally {
            IOUtils.close(o)
            IOUtils.close(stream)
        }
    }

    /**
     * move file
     */
    fun moveFile(sourceFilePath: String?, destFilePath: String?) {
        if (TextUtils.isEmpty(sourceFilePath) || TextUtils.isEmpty(destFilePath)) {
            throw RuntimeException("Both sourceFilePath and destFilePath cannot be null.")
        }
        moveFile(File(sourceFilePath), File(destFilePath))
    }

    /**
     * move file
     */
    fun moveFile(srcFile: File, destFile: File) {
        val rename = srcFile.renameTo(destFile)
        if (!rename) {
            copyFile(srcFile.absolutePath, destFile.absolutePath)
            deleteFile(srcFile.absolutePath)
        }
    }

    /**
     * copy file
     *
     * @throws RuntimeException if an error occurs while operator FileOutputStream
     */
    @JvmStatic
    fun copyFile(sourceFilePath: String?, destFilePath: String?): Boolean {
        var inputStream: InputStream? = null
        inputStream = try {
            FileInputStream(sourceFilePath)
        } catch (e: FileNotFoundException) {
            throw RuntimeException("FileNotFoundException occurred. ", e)
        }
        return writeFile(destFilePath, inputStream)
    }

    /**
     * read file to string list, a element of list is a line
     *
     * @param charsetName The name of a supported [ &lt;/code&gt;charset&lt;code&gt;][java.nio.charset.Charset]
     * @return if file not exist, return null, else return content of file
     * @throws RuntimeException if an error occurs while operator BufferedReader
     */
    fun readFileToList(filePath: String?, charsetName: String?): List<String?>? {
        val file = File(filePath)
        val fileContent: MutableList<String?> = ArrayList()
        if (file == null || !file.isFile) {
            return null
        }
        var reader: BufferedReader? = null
        return try {
            val `is` = InputStreamReader(FileInputStream(file), charsetName)
            reader = BufferedReader(`is`)
            var line: String? = null
            while (reader.readLine().also { line = it } != null) {
                fileContent.add(line)
            }
            fileContent
        } catch (e: IOException) {
            throw RuntimeException("IOException occurred. ", e)
        } finally {
            IOUtils.close(reader)
        }
    }

    /**
     * get file name from path, not include suffix
     *
     * <pre>
     * getFileNameWithoutExtension(null)               =   null
     * getFileNameWithoutExtension("")                 =   ""
     * getFileNameWithoutExtension("   ")              =   "   "
     * getFileNameWithoutExtension("abc")              =   "abc"
     * getFileNameWithoutExtension("a.mp3")            =   "a"
     * getFileNameWithoutExtension("a.b.rmvb")         =   "a.b"
     * getFileNameWithoutExtension("c:\\")              =   ""
     * getFileNameWithoutExtension("c:\\a")             =   "a"
     * getFileNameWithoutExtension("c:\\a.b")           =   "a"
     * getFileNameWithoutExtension("c:a.txt\\a")        =   "a"
     * getFileNameWithoutExtension("/home/admin")      =   "admin"
     * getFileNameWithoutExtension("/home/admin/a.txt/b.mp3")  =   "b"
    </pre> *
     *
     * @return file name from path, not include suffix
     * @see
     */
    fun getFileNameWithoutExtension(filePath: String): String {
        if (StringUtils.isEmpty(filePath)) {
            return filePath
        }
        val extenPosi = filePath.lastIndexOf(FILE_EXTENSION_SEPARATOR)
        val filePosi = filePath.lastIndexOf(File.separator)
        if (filePosi == -1) {
            return if (extenPosi == -1) filePath else filePath.substring(0, extenPosi)
        }
        if (extenPosi == -1) {
            return filePath.substring(filePosi + 1)
        }
        return if (filePosi < extenPosi) filePath.substring(filePosi + 1, extenPosi) else filePath.substring(filePosi + 1)
    }

    /**
     * get file name from path, include suffix
     *
     * <pre>
     * getFileName(null)               =   null
     * getFileName("")                 =   ""
     * getFileName("   ")              =   "   "
     * getFileName("a.mp3")            =   "a.mp3"
     * getFileName("a.b.rmvb")         =   "a.b.rmvb"
     * getFileName("abc")              =   "abc"
     * getFileName("c:\\")              =   ""
     * getFileName("c:\\a")             =   "a"
     * getFileName("c:\\a.b")           =   "a.b"
     * getFileName("c:a.txt\\a")        =   "a"
     * getFileName("/home/admin")      =   "admin"
     * getFileName("/home/admin/a.txt/b.mp3")  =   "b.mp3"
    </pre> *
     *
     * @return file name from path, include suffix
     */
    fun getFileName(filePath: String): String {
        if (StringUtils.isEmpty(filePath)) {
            return filePath
        }
        val filePosi = filePath.lastIndexOf(File.separator)
        val fileName = if (filePosi == -1) filePath.replace("?", "") else filePath.substring(filePosi + 1).replace("?", "")
        return if (StringUtils.inputJudge(fileName)) {
            fileName.md5() + "." + getFileExtension(fileName)
        } else fileName
    }

    /**
     * get folder name from path
     *
     * <pre>
     * getFolderName(null)               =   null
     * getFolderName("")                 =   ""
     * getFolderName("   ")              =   ""
     * getFolderName("a.mp3")            =   ""
     * getFolderName("a.b.rmvb")         =   ""
     * getFolderName("abc")              =   ""
     * getFolderName("c:\\")              =   "c:"
     * getFolderName("c:\\a")             =   "c:"
     * getFolderName("c:\\a.b")           =   "c:"
     * getFolderName("c:a.txt\\a")        =   "c:a.txt"
     * getFolderName("c:a\\b\\c\\d.txt")    =   "c:a\\b\\c"
     * getFolderName("/home/admin")      =   "/home"
     * getFolderName("/home/admin/a.txt/b.mp3")  =   "/home/admin/a.txt"
    </pre> *
     */
    fun getFolderName(filePath: String): String {
        if (StringUtils.isEmpty(filePath)) {
            return filePath
        }
        val filePosi = filePath.lastIndexOf(File.separator)
        return if (filePosi == -1) "" else filePath.substring(0, filePosi)
    }

    /**
     * get suffix of file from path
     *
     * <pre>
     * getFileExtension(null)               =   ""
     * getFileExtension("")                 =   ""
     * getFileExtension("   ")              =   "   "
     * getFileExtension("a.mp3")            =   "mp3"
     * getFileExtension("a.b.rmvb")         =   "rmvb"
     * getFileExtension("abc")              =   ""
     * getFileExtension("c:\\")              =   ""
     * getFileExtension("c:\\a")             =   ""
     * getFileExtension("c:\\a.b")           =   "b"
     * getFileExtension("c:a.txt\\a")        =   ""
     * getFileExtension("/home/admin")      =   ""
     * getFileExtension("/home/admin/a.txt/b")  =   ""
     * getFileExtension("/home/admin/a.txt/b.mp3")  =   "mp3"
    </pre> *
     */
    fun getFileExtension(filePath: String): String {
        if (StringUtils.isBlank(filePath)) {
            return filePath
        }
        val extenPosi = filePath.lastIndexOf(FILE_EXTENSION_SEPARATOR)
        val filePosi = filePath.lastIndexOf(File.separator)
        if (extenPosi == -1) {
            return ""
        }
        return if (filePosi >= extenPosi) "" else filePath.substring(extenPosi + 1)
    }

    /**
     * Creates the directory named by the trailing filename of this file, including the complete
     * directory path required
     * to create this directory. <br></br>
     * <br></br>
     *
     * **Attentions:**
     *  * makeDirs("C:\\Users\\Trinea") can only create users folder
     *  * makeFolder("C:\\Users\\Trinea\\") can create Trinea folder
     *
     *
     * @return true if the necessary directories have been created or the target directory already
     * exists, false one of
     * the directories can not be created.
     *
     *  * if [FileUtils.getFolderName] return null, return false
     *  * if target directory already exists, return true
     *  * return
     *
     */
    fun makeDirs(filePath: String): Boolean {
        val folderName = getFolderName(filePath)
        if (StringUtils.isEmpty(folderName)) {
            return false
        }
        val folder = File(folderName)
        return if (folder.exists() && folder.isDirectory) true else folder.mkdirs()
    }

    /**
     * @see .makeDirs
     */
    fun makeFolders(filePath: String): Boolean {
        return makeDirs(filePath)
    }

    /**
     * Indicates if this file represents a file on the underlying file system.
     */
    @JvmStatic
    fun isFileExist(filePath: String?): Boolean {
        if (StringUtils.isBlank(filePath)) {
            return false
        }
        val file = File(filePath)
        return file.exists() && file.isFile
    }

    /**
     * Indicates if this file represents a directory on the underlying file system.
     */
    fun isFolderExist(directoryPath: String?): Boolean {
        if (StringUtils.isBlank(directoryPath)) {
            return false
        }
        return directoryPath?.let {
           return File(directoryPath).run {
               exists() && isDirectory
           }
        }?:false

    }

    /**
     * delete file or directory
     *
     *  * if path is null or empty, return true
     *  * if path not exist, return true
     *  * if path exist, delete recursion. return true
     *
     */
    fun deleteFile(path: String): Boolean {
        if (StringUtils.isBlank(path)) {
            return true
        }
        val file = File(path)
        if (!file.exists()) {
            return true
        }
        if (file.isFile) {
            return file.delete()
        }
        if (!file.isDirectory) {
            return false
        }
        file.listFiles()?.forEach {
            if (it.isFile) {
                it.delete()
            } else if (it.isDirectory) {
                deleteFile(it.absolutePath)
            }
        }

        return file.delete()
    }

    /**
     * get file size
     *
     *  * if path is null or empty, return -1
     *  * if path exist and it is a file, return file size, else return -1
     *
     *
     * @return returns the length of this file in bytes. returns -1 if the file does not exist.
     */
    fun getFileSize(path: String?): Long {
        if (StringUtils.isBlank(path)) {
            return -1
        }
        val file = File(path)
        return if (file.exists() && file.isFile) file.length() else -1
    }

    /**
     * 删除指定目录中特定的文件
     */
    fun delete(dir: String, filter: FilenameFilter?) {
        val file = File(dir)
        when{
            file.exists()->{
                if(file.isFile){
                    file.delete()
                }else if(file.isDirectory){
                    file.listFiles(filter)?.forEach {
                        if (it.isFile) {
                            it.delete()
                        }
                    }

                }
            }
            else ->{

            }
        }

    }

    /**
     * 将文件转byte
     */
    fun readFileToByteArray(file: File): ByteArray? {
        val bos = ByteArrayOutputStream(file.length().toInt())
        var `in`: BufferedInputStream? = null
        try {
            `in` = BufferedInputStream(FileInputStream(file))
            val buf_size = 1024
            val buffer = ByteArray(buf_size)
            var len = 0
            while (-1 != `in`.read(buffer, 0, buf_size).also { len = it }) {
                bos.write(buffer, 0, len)
            }
            return bos.toByteArray()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                `in`!!.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            try {
                bos.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return null
    }

    /**
     * 采用NIO将文件转byte
     */
    fun readFileToByteArrayByNIO(file: File?): ByteArray? {
        var channel: FileChannel? = null
        var fs: FileInputStream? = null
        try {
            fs = FileInputStream(file)
            channel = fs.channel
            val byteBuffer = ByteBuffer.allocate(channel.size().toInt())
            while (channel.read(byteBuffer) > 0) {
                // do nothing
                // System.out.println("reading");
            }
            return byteBuffer.array()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                channel!!.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            try {
                fs!!.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return null
    }

    /**
     * 处理大文件时优化
     * @param file
     * @return
     */
    fun readBigFileToByteArray(file: File): ByteArray? {
       return RandomAccessFile(file.absolutePath, "r").channel.use {
            val byteBuffer = it.map(FileChannel.MapMode.READ_ONLY, 0, it.size())
            val result = ByteArray(it.size().toInt())
            if(byteBuffer.remaining()>0){
                byteBuffer[result, 0, byteBuffer.remaining()]
            }
            result
        }
    }

    //复制沙盒私有文件到Download公共目录下
    //orgFilePath是要复制的文件私有目录路径
    //displayName复制后文件要显示的文件名称带后缀（如xx.txt）
    @RequiresApi(api = Build.VERSION_CODES.Q)
    fun copyPrivateApkToDownload(context: Context, orgFilePath: String, displayName: String) {
        val values = ContentValues()
        //values.put(MediaStore.Images.Media.DESCRIPTION, "This is a file");
        values.put(MediaStore.Files.FileColumns.DISPLAY_NAME, displayName)
        values.put(MediaStore.Files.FileColumns.MIME_TYPE, "application/vnd.android.package-archive") //MediaStore对应类型名
        values.put(MediaStore.Files.FileColumns.TITLE, displayName)
        values.put(MediaStore.Images.Media.RELATIVE_PATH, "Download/Apks") //公共目录下目录名
        val external = MediaStore.Downloads.EXTERNAL_CONTENT_URI //内部存储的Download路径
        val resolver = context.contentResolver
        val insertUri = resolver.insert(external, values) //使用ContentResolver创建需要操作的文件
        //Log.i("Test--","insertUri: " + insertUri);
        var ist: InputStream? = null
        var ost: OutputStream? = null
        try {
            ist = FileInputStream(File(orgFilePath))
            if (insertUri != null) {
                ost = resolver.openOutputStream(insertUri)
            }
            if (ost != null) {
                val buffer = ByteArray(4096)
                var byteCount = 0
                while (ist.read(buffer).also { byteCount = it } != -1) {  // 循环从输入流读取 buffer字节
                    ost.write(buffer, 0, byteCount) // 将读取的输入流写入到输出流
                }
                // write what you want
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                ist?.close()
                ost?.close()
            } catch (e: IOException) {
                //Log.i("copyPrivateToDownload--","fail in close: " + e.getCause());
            }
        }
    }


}