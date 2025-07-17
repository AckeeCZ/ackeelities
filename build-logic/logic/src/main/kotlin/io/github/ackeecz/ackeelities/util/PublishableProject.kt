package io.github.ackeecz.ackeelities.util

internal sealed interface PublishableProject {

    val projectName: String

    object Bom : PublishableProject {

        override val projectName = "bom"
    }

    object Core : PublishableProject {

        override val projectName = "core"
    }
}
