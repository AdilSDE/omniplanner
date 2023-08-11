/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/* $Id: ImageConverterG2D2SVG.java 1664570 2015-03-06 09:41:07Z lbernardo $ */

package org.apache.fop.image.loader.batik;

import java.awt.Dimension;
import java.awt.geom.Rectangle2D;
import java.util.Map;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.apache.batik.anim.dom.SVGDOMImplementation;
import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGeneratorContext;
import org.apache.batik.svggen.SVGGraphics2D;

import org.apache.xmlgraphics.image.loader.Image;
import org.apache.xmlgraphics.image.loader.ImageException;
import org.apache.xmlgraphics.image.loader.ImageFlavor;
import org.apache.xmlgraphics.image.loader.ImageSize;
import org.apache.xmlgraphics.image.loader.impl.AbstractImageConverter;
import org.apache.xmlgraphics.image.loader.impl.ImageGraphics2D;
import org.apache.xmlgraphics.image.loader.impl.ImageXMLDOM;

/**
 * This ImageConverter converts Java2D images into SVG images.
 */
public class ImageConverterG2D2SVG extends AbstractImageConverter {

    /** {@inheritDoc} */
    public Image convert(Image src, Map hints) throws ImageException {
        checkSourceFlavor(src);
        ImageGraphics2D g2dImage = (ImageGraphics2D)src;

        DOMImplementation domImpl = GenericDOMImplementation.getDOMImplementation();

        // Create an instance of org.w3c.dom.Document
        Document document = domImpl.createDocument(
                SVGDOMImplementation.SVG_NAMESPACE_URI, "svg", null);
        Element root = document.getDocumentElement();

        // Create an SVGGeneratorContext to customize SVG generation
        SVGGeneratorContext genCtx = SVGGeneratorContext.createDefault(document);
        genCtx.setComment("Generated by Apache Batik's SVGGraphics2D");
        genCtx.setEmbeddedFontsOn(true);

        // Create an instance of the SVG Generator
        SVGGraphics2D g2d = new SVGGraphics2D(genCtx, true);
        ImageSize size = src.getSize();
        Dimension dim = size.getDimensionMpt();
        g2d.setSVGCanvasSize(dim);
        //SVGGraphics2D doesn't generate the viewBox by itself
        root.setAttribute("viewBox", "0 0 " + dim.width + " " + dim.height);

        g2dImage.getGraphics2DImagePainter().paint(g2d,
                new Rectangle2D.Float(0, 0, dim.width, dim.height));
        //Populate the document root with the generated SVG content.
        g2d.getRoot(root);

        //Return the generated SVG image
        ImageXMLDOM svgImage = new ImageXMLDOM(src.getInfo(), document, BatikImageFlavors.SVG_DOM);
        g2d.dispose();
        return svgImage;
    }

    /** {@inheritDoc} */
    public ImageFlavor getSourceFlavor() {
        return ImageFlavor.GRAPHICS2D;
    }

    /** {@inheritDoc} */
    public ImageFlavor getTargetFlavor() {
        return BatikImageFlavors.SVG_DOM;
    }

}
