

package com.flofriday.constantEvalKotlinPlugin

import org.gradle.api.file.RegularFileProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property

open class ConstantEvalGradleExtension(objects: ObjectFactory) {
  val stringProperty: Property<String> = objects.property(String::class.java)
  val fileProperty: RegularFileProperty = objects.fileProperty()
}
