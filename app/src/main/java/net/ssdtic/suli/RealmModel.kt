package net.ssdtic.suli

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class ClassroomDivide(
        @PrimaryKey open var id: Int = -1,
        open var building_id: Int = -1,
        open var place: String = "",
        open var weekday: Int = -1,
        open var time: Int = -1,
        open var cell_text: String = "",
        open var cell_color: String = "",
        open var classname: String = "",
        open var person: String = "",
        open var department: String = "",
        open var class_code: String = ""
) : RealmObject() {}

open class Building(
        @PrimaryKey open var id: Int = -1,
        open var building_name: String = "",
        open var url: String = "",
        open var color: String = ""
) : RealmObject() {}

open class SyllabusForm(
        @PrimaryKey open var id: Int = -1,
        open var form: String = "",
        open var display: String = "",
        open var value: String = ""
) : RealmObject() {}

open class CancelInfo(
        @PrimaryKey open var id: Int = -1,
        open var date: String = "",
        open var time: String = "",
        open var classification: String = "",
        open var department: String = "",
        open var classname: String = "",
        open var person: String = "",
        open var place: String = "",
        open var note: String = ""
) : RealmObject() {}
