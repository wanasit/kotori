package com.github.wanasit.kotori.benchmark.dataset

import com.github.wanasit.kotori.dictionary.utils.downloadIntoDirectory
import com.github.wanasit.kotori.dictionary.utils.extractIntoDirectory
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import java.io.File
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory


/**
 * Livedoor News Dataset
 * From: https://www.rondhuit.com/download_en.html
 *
 * Licence: Creative Commons license (display – revision prohibited) applies to each article file.
 */
object LivedoorNewsDataset {

    const val DEFAULT_DATA_DIR = "../data/livedoor-news"
    private const val DATA_DOWNLOAD_URL = "https://www.rondhuit.com/download/livedoor-news-data.tar.gz"

    data class LivedoorNewsEntry(
            val url: String,
            val title: String,
            val category: String,
            val body: String): TextDatasetEntry{
        override val text = body;
    }

    fun loadDataset(dataDir: String = DEFAULT_DATA_DIR): List<LivedoorNewsEntry> {
        val xmlFiles = listXmlFiles(dataDir)
        if (xmlFiles.isEmpty()) {
            throw IllegalStateException(
                    "Can't find dataset in ${dataDir}. You may need to call download() into that directory first.")
        }

        return xmlFiles.flatMap { parseDataEntries(it) }
    }

    fun download(
            dataDir: String = DEFAULT_DATA_DIR,
            overwrite: Boolean = false
    ) {
        if (listXmlFiles(dataDir).any() && !overwrite) {
            return
        }

        val dataDirFile = File(dataDir)
        dataDirFile.mkdirs()

        val downloadedFile = downloadIntoDirectory(dataDirFile, DATA_DOWNLOAD_URL)
        extractIntoDirectory(dataDirFile, downloadedFile)
    }

    @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    private fun listXmlFiles(dataDir: String): List<File> {
        val dataDirFile = File(dataDir)
        dataDirFile.mkdirs()
        return dataDirFile.listFiles().filter { it.extension == "xml" }
    }

    private var factory: DocumentBuilderFactory = DocumentBuilderFactory.newInstance()
    private var builder: DocumentBuilder = factory.newDocumentBuilder()

    private fun parseDataEntries(xmlFile: File): List<LivedoorNewsEntry> {
        /**
         * Expected data format:
         * <doc>
         *   <field name="url">http://news.livedoor.com/article/detail/4778030/</field>
         *   <field name="cat">dokujo-tsushin</field>
         *   ...
         * </doc>
         * <doc>
         */
        val root: Document = builder.parse(xmlFile.inputStream())
        val docNodes = root.getElementsByTagName("doc")

        return docNodes.asList().map { docNode ->
            var url: String? = null
            var title: String? = null
            var category: String? = null
            val body = mutableListOf<String>()

            docNode.childElements.forEach {
                when(it.getAttribute("name")) {
                    "title" -> title = it.textContent
                    "url" -> url = it.textContent
                    "cat" -> category = it.textContent
                    "body" -> body.add(it.textContent)
                    else -> {}
                }
            }

            LivedoorNewsEntry(
                    url ?: "",
                    title?: "",
                    category?: "",
                    body.joinToString("\n"))
        }
    }

    val Node.childElements: List<Element>
        get() = this.childNodes.asList()
                .filter { it.nodeType == Node.ELEMENT_NODE }
                .map { it as Element }

    private fun NodeList.asList(): List<Node> {
        val list = mutableListOf<Node>()
        for (i in 0 until this.length) {
            list.add(this.item(i))
        }
        return list
    }
}