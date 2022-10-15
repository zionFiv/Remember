package com.zion.remember.game

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.GridItemSpan
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import com.zion.remember.util.SudoUtil

class NumberGameActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            jetPackCompose()
        }
    }

    @OptIn(
        ExperimentalFoundationApi::class,
        androidx.compose.material.ExperimentalMaterialApi::class
    )
    @Composable
    fun jetPackCompose() {
        val showDialog = remember {//显示数字布局
            mutableStateOf(false)
        }
        val numArray = mutableListOf(
            5, 3, 0, 0, 7, 0, 0, 0, 0,
            6, 0, 0, 1, 9, 5, 0, 0, 0,
            0, 9, 8, 0, 0, 0, 0, 6, 0,
            8, 0, 0, 0, 6, 0, 0, 0, 3,
            4, 0, 0, 8, 0, 3, 0, 0, 1,
            7, 0, 0, 0, 2, 0, 0, 0, 6,
            0, 6, 0, 0, 0, 0, 2, 8, 0,
            0, 0, 0, 4, 1, 9, 0, 0, 5,
            0, 0, 0, 0, 8, 0, 0, 7, 9
        )
        val numberList = remember {//更新后的列表
            mutableStateOf(numArray)
        }

        val numberState = remember {
            mutableStateOf(1)
        }

        val selectColor = remember{
            mutableStateOf(Color(0XFFF6F6F6))
        }



        LazyVerticalGrid(
            cells = GridCells.Fixed(9),
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.Blue),
            contentPadding = PaddingValues(20.dp)

        ) {
            items(9) {
                Text(
                    text = "",
                    modifier = Modifier.size(100.dp),
                    color = Color.Red
                )
            }

            Log.d("TAG", "${numberList.value} ${numberState.value}")
            items(numberList.value.size) { item ->
                Card(
                    backgroundColor = if(item == numberState.value) selectColor.value else Color(0xfff6f6f6),
                    modifier = Modifier
                        .size(35.dp)
                        .padding(
                            end = if (item % 3 == 2) 0.5.dp else 0.dp,
                            bottom = if (item % 27 in 18..26) 0.5.dp else 0.dp
                        ),
                    shape = MaterialTheme.shapes.large,
                    onClick = {
                        if(numArray[item] == 0) {
                            showDialog.value = true
                            selectColor.value = Color(0xffdddddd)
                            numberState.value = item
                        }

                    }

                ) {
                    Text(
                        text = "${if (numberList.value[item] == 0) "" else numberList.value[item]}",
                        fontSize = 12.sp,
                        fontStyle = if(numArray[item] != 0)FontStyle.Normal else FontStyle.Italic,
                        fontWeight = if(numArray[item] != 0)FontWeight.Bold else FontWeight.Normal,
                        textAlign =TextAlign.Center,
                        color = Color(0xFF666666),
                        modifier = Modifier.wrapContentHeight(align = Alignment.CenterVertically)
                    )
                }

            }

            items(18) {
                Text(
                    text = "",
                    modifier = Modifier.size(100.dp),
                    color = Color.Red
                )
            }
        }


        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {

            Column(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "数独",
                    fontSize = 20.sp,
                    color = Color(0xFF333333),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.clickable(true, onClick = {
                        val list = numberList.value
                        SudoUtil.solveSudo(list)
                    }))

            }

        }


        if (showDialog.value) {
            Popup(alignment = Alignment.BottomCenter) {
                LazyVerticalGrid(
                    cells = GridCells.Fixed(5),
                    contentPadding = PaddingValues(60.dp),

                    )
                {
                    items(10) { number ->
                        Card(
                            onClick = {
                                numberList.value[numberState.value] = number

                                showDialog.value = false

                                if(!isValidSudo(numberList.value)) {
                                    selectColor.value = Color(0xffd30775)
                                } else {
                                    selectColor.value = Color(0xfff6f6f6)
                                }
                               Log.d("Tag",  "${isValidSudo(numberList.value)}")
                            },
                            modifier = Modifier.size(35.dp),
                            backgroundColor = Color(0xFFF3F3F3)

                        ) {
                            Text(
                                text = "${if(number == 0) "DEL" else number}",
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center,
                                color = Color(0xFF666666),
                                modifier = Modifier.wrapContentHeight(align = Alignment.CenterVertically)
                            )
                        }
                    }
                }
            }
        }
    }


    @Preview
    @Composable
    fun Preview() {
        jetPackCompose()
    }

    /**
     * TIP
     *
     * 对应MATCH_PARENT?
     * 显示dialog只能放在compose函数里？
     * dialog消失的时候不会自动刷新
     * column 不能嵌grid
     * 点击solveSudo后，列表更新会把最终答案带过来，但是有些错误
     * 如果一进入就点击solveSudo，列表不能点击
     */

    private fun isValidSudo(datas: MutableList<Int>): Boolean {
        var suduArray = mutableListOf<MutableList<Int>>()
        val totalRow = datas.size / 9
        for (i in 0 until totalRow)
            suduArray.add(datas.subList(0 + 9 * i, 9 + 9 * i))
        var rows = Array(9) { IntArray(9) }
        var columns = Array(9) { IntArray(9) }
        var subboxes = Array(3) { Array(3) { IntArray(9) } }
        for (j in 0 until 9) {
            for (k in 0 until 9) {
                var index = suduArray[j][k] - 1
                if (index != -1) {
                    rows[j][index]++
                    columns[k][index]++
                    subboxes[j / 3][k / 3][index]++
                    if (rows[j][index] > 1 || columns[k][index] > 1 || subboxes[j / 3][k / 3][index] > 1) {
                        return false
                    }
                }
            }


        }

        return true
    }


}