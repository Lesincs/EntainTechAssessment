import com.lesincs.convention.configureDetekt
import com.lesincs.convention.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.kotlin.dsl.getByType

class AndroidDetektConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply(
                libs.findPlugin("detekt").get().get().pluginId
            )
            val extension = extensions.getByType<DetektExtension>()
            configureDetekt(extension)
        }
    }
}