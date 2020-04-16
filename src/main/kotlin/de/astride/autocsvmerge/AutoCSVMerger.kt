package de.astride.autocsvmerge

import java.io.File
import java.util.stream.Collectors
import java.util.stream.IntStream
import kotlin.system.exitProcess


typealias HeaderKey = String//Div,Date,Time,...
typealias Line = List<String>//D1,16/08/2019,19:30,...
typealias DataPoints = List<String>//D1,D1,D1,D1,...

fun main(args: Array<String>) {
    val folder = File(args.firstOrNull() ?: "data")
    if (!folder.exists()) exitProcess(981)
    if (!folder.isDirectory) exitProcess(982)

    val memory = mutableMapOf<HeaderKey, DataPoints>() //key, list of all values
    val separator = ","

    folder.listFiles()?.filter { it.name.endsWith(".csv") }?.forEach { file ->
        val readLines = file.reader().readLines()
        val header = readLines.first().split(separator)
        val lines = readLines.drop(1).map { it.split(separator) }
        header.indices.forEach { id ->
            val key = header[id]
//            output[key] = (output[key] ?: emptyList()) + lines[id]
            lines.forEach {
                memory[key] = (memory[key] ?: emptyList()) + it[id]
            }
        }
    }

    val outputFile = File("auto-csv-merge-output-${System.currentTimeMillis()}.csv")
    val outputText =
        "${memory.keys.joinToString(separator)}\n${ transpose(memory.values).joinToString("\n") { it.joinToString(separator) }}"
    outputFile.writeText(outputText)

}
fun <T> transpose(list: Collection<List<T>>): List<List<T>> {
    val max = list.map { it.size }.max() ?: -1
    val iterList = list.map { it.iterator() }
    return IntStream.range(0, max)
        .mapToObj { _: Int ->
            iterList
                .filter { it.hasNext() }
                .map { m: Iterator<T> -> m.next() }
        }
        .collect(Collectors.toList())
}

