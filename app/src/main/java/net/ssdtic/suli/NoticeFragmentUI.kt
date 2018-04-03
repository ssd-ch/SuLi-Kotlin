package net.ssdtic.suli

import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.coroutines.onClick

class NoticeFragmentUI : AnkoComponent<NoticeFragment> {
    override fun createView(ui: AnkoContext<NoticeFragment>) = with(ui) {
        verticalLayout {
            val name = editText()
            button("Say Hello") {
                onClick { ctx.toast("Hello, ${name.text}!") }
            }
        }
    }
}