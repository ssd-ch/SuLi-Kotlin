package net.ssdtic.suli

class UpdateAllData {

    companion object {

        fun start(completeHandler: () -> Unit, errorHandler: (String) -> Unit) {

            println("UpdateAllData : start task")

            val completeClosure = {
                println("UpdateAllData : semaphore signal")
            }

            val errorClosure = {
                errorHandler("error")
                println("UpdateAllData : semaphore signal (error)")
            }

            GetSyllabusForm.start({ completeClosure() }, { error -> errorClosure()})

            GetClassroomDivide.start({ completeClosure() }, { error -> errorClosure()})

            GetCancelInfo.start({ completeClosure() }, { error -> errorClosure()})

            completeHandler()

        }

        fun cancel() {
            println("UpdateAllData : canceled task")
        }
    }
}