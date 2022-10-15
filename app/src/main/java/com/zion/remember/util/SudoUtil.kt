package com.zion.remember.util

import android.util.Log

object SudoUtil {
    var sudoArray = mutableListOf<MutableList<Int>>()
    private val spaces: MutableList<IntArray> = mutableListOf()
    var valid = false
    var rows = Array(9) { BooleanArray(9) }
    var columns = Array(9) { BooleanArray(9) }
    var subboxes = Array(3) { Array(3) { BooleanArray(9) } }
    fun solveSudo(datas: MutableList<Int>) {
        spaces.clear()
        sudoArray.clear()
        valid = false

        val totalRow = datas.size / 9
        for (i in 0 until totalRow)
            sudoArray.add(datas.subList(0 + 9 * i, 9 + 9 * i))
        for (j in 0 until 9) {
            for (k in 0 until 9) {
                if (sudoArray[j][k] != 0) {
                    var digit = sudoArray[j][k] - 1
                    rows[j][digit] = true
                    columns[k][digit] = true
                    subboxes[j / 3][k / 3][digit] = true
                } else {
                    spaces.add(intArrayOf(j, k))
                }
            }
        }
        dfs(sudoArray, 0)
    }

    private fun dfs(board: MutableList<MutableList<Int>>, pos: Int) {

        if (pos == spaces.size) {
            valid = true
            Log.d("TAG", "board : $board")
            return
        }

        var space = spaces.get(pos)
        val i = space[0]
        var j = space[1]

        if (!valid) {
            for (digit in 0 until 9) {
                if (!rows[i][digit] && !columns[j][digit] && !subboxes[i / 3][j / 3][digit]) {
                    rows[i][digit] = true
                    columns[j][digit] = true
                    subboxes[i / 3][j / 3][digit] = true
                    board[i][j] = digit + 1
                    dfs(board, pos + 1)
                    rows[i][digit] = false
                    columns[j][digit] = false
                    subboxes[i / 3][j / 3][digit] = false
                }
            }
        }

    }


}