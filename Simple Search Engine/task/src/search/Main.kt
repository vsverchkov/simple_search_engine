package search

import java.io.File

fun main(args: Array<String>) {
    val fileName = getFileName(args)
    if (fileName.isBlank()) {
        println("Please, add '--data' argument with path to the data")
    }
    // TODO: collect loading and indexing data
    val data = loadData(fileName)
    val index = indexData(data)
    process(data, index)
}

fun getFileName(args: Array<String>): String {
    for (i in args.indices) {
        if ("--data" == args[i]){
            return args[i + 1]
        }
    }
    return ""
}

fun loadData(fileName: String): List<String> {
    return File(fileName).readLines()
}

fun indexData(data: List<String>): Map<String, Set<Int>> {
    val index = HashMap<String, MutableSet<Int>>()
    for (i in data.indices) {
        val recordWords = data[i].split(' ')
        for (j in recordWords.indices) {
            val word = recordWords[j].toLowerCase()
            val existLines = index[word]
            if (existLines == null) {
                val newExistLines = HashSet<Int>()
                newExistLines.add(i)
                index[word] = newExistLines
                continue
            }
            existLines.add(i)
        }
    }
    return index
}

fun process(data: List<String>, index: Map<String, Set<Int>>) {
    while (true) {
        println("""
            === Menu ===
            1. Find a person
            2. Print all people
            0. Exit
        """.trimIndent())
        when (readln().toInt()) {
            0 -> break
            1 -> findAll(data, index)
            2 -> printAll(data)
            else -> println("Please, choose available options")
        }
    }
}

const val ALL = "ALL"
const val ANY = "ANY"
const val NONE = "NONE"
val STRATEGY = listOf(ALL, ANY, NONE)

fun findAll(data: List<String>, index: Map<String, Set<Int>>) {
    val strategy = readln().toUpperCase()
    val patternLowerCase = readln().toLowerCase().split(' ')

    if (!STRATEGY.contains(strategy)) {
        println("Strategy not found. Available: $STRATEGY")
        return
    }

    val resultIndexes: Set<Int> = when (strategy) {
        ALL -> findIncludeAll(patternLowerCase, index)
        ANY -> findIncludeAny(patternLowerCase, index)
        NONE -> findExcludeAny(patternLowerCase, index, data)
        else -> throw RuntimeException("Invalid strategy: $strategy Available: $STRATEGY")
    }

    if (resultIndexes.isEmpty()) {
        println("No matching people found.")
        return
    }

    resultIndexes.forEach {
        println(data[it])
    }
}

fun findIncludeAll(patternLowerCase: List<String>, index: Map<String, Set<Int>>): Set<Int> {
    var resultIndexes = emptySet<Int>()
    for (i in patternLowerCase.indices) {
        val indexesOfData = index[patternLowerCase[i]] ?: emptySet()
        if (indexesOfData.isEmpty()) {
            resultIndexes = emptySet()
            break
        }
        if (i == 0) {
            resultIndexes = indexesOfData
            continue
        }
        resultIndexes = resultIndexes.intersect(indexesOfData)
    }
    return resultIndexes
}

fun findIncludeAny(patternLowerCase: List<String>, index: Map<String, Set<Int>>): Set<Int> {
    var resultIndexes = emptySet<Int>()
    for (i in patternLowerCase.indices) {
        val indexesOfData = index[patternLowerCase[i]] ?: emptySet()
        if (indexesOfData.isEmpty()) {
            resultIndexes = emptySet()
        }
        if (i == 0) {
            resultIndexes = indexesOfData
            continue
        }
        resultIndexes = resultIndexes.union(indexesOfData)
    }
    return resultIndexes
}

private fun findExcludeAny(patternLowerCase: List<String>, index: Map<String, Set<Int>>, data: List<String>): Set<Int> {
    var resultIndexes = findIncludeAny(patternLowerCase, index)
    resultIndexes = data.indices.toSet().subtract(resultIndexes)
    return resultIndexes
}

fun printAll(data: List<String>) {
    data.forEach {
        println(it)
    }
}
