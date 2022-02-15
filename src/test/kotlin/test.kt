import com.lderic.schoology.Schoology
import com.lderic.schoology.print
import kotlinx.coroutines.runBlocking


fun main() = runBlocking {
    val schoology = Schoology("duoli24@js-student.org", "Eric52coco")

    schoology.login()

    schoology.cookies.print()
    schoology.cookies.filter { it.name.startsWith("SESS") }[0].print()
}
