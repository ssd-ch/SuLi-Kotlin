package net.ssdtic.suli


import org.jetbrains.anko.*

class NoticeFragmentUI : AnkoComponent<NoticeFragment> {

    override fun createView(ui: AnkoContext<NoticeFragment>) = with(ui) {

        verticalLayout {

            textView {
                text = "NoticeFragment"
            }

        }
    }
}