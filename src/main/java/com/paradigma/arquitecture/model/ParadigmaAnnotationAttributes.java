package com.paradigma.arquitecture.model;

import org.springframework.core.annotation.AnnotationAttributes;

/**
 * The Class ParadigmaAnnotationAttributes.
 *
 * @author Javier Ledo Vázquez
 * @version 1.0
 */
public class ParadigmaAnnotationAttributes {

	public ParadigmaAnnotationAttributes(AnnotationAttributes annotationAttributes) {
		super();
		this.annotationAttributes = annotationAttributes;
	}

	private AnnotationAttributes annotationAttributes;

	public AnnotationAttributes getAnnotationAttributes() {
		return annotationAttributes;
	}

}
