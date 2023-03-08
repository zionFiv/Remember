package com.zion.remember.http

data class BaseData<T> (
    var code  : Int = -1,
    var msg : String = "",
    var data : T?= null
)