package org.apache.lucene.xmlparser.builders;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.spans.SpanOrQuery;
import org.apache.lucene.search.spans.SpanQuery;
import org.apache.lucene.search.spans.SpanTermQuery;
import org.apache.lucene.xmlparser.DOMUtils;
import org.apache.lucene.xmlparser.ParserException;
import org.w3c.dom.Element;
/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public class SpanOrTermsBuilder extends SpanBuilderBase
{
    Analyzer analyzer;
    
    
    /**
     * @param analyzer
     */
    public SpanOrTermsBuilder(Analyzer analyzer)
    {
        super();
        this.analyzer = analyzer;
    }
	public SpanQuery getSpanQuery(Element e) throws ParserException
	{
 		String fieldName=DOMUtils.getAttributeWithInheritanceOrFail(e,"fieldName");
 		String value=DOMUtils.getNonBlankTextOrFail(e);
		
		try
		{
			ArrayList clausesList=new ArrayList();
			TokenStream ts=analyzer.tokenStream(fieldName,new StringReader(value));
			final Token reusableToken = new Token();
	                for (Token nextToken = ts.next(reusableToken); nextToken != null; nextToken = ts.next(reusableToken)) {
			    SpanTermQuery stq=new SpanTermQuery(new Term(fieldName,nextToken.term()));
			    clausesList.add(stq);
			}
			SpanOrQuery soq=new SpanOrQuery((SpanQuery[]) clausesList.toArray(new SpanQuery[clausesList.size()]));
			soq.setBoost(DOMUtils.getAttribute(e,"boost",1.0f));
			return soq;
		}
		catch(IOException ioe)
		{
		    throw new ParserException("IOException parsing value:"+value);
		}
	}

}
