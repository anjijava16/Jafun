package com.practicalHadoop.lucene.spatial.geometry.filters;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

import com.practicalHadoop.lucene.spatial.GeometricShapeHelper;
import com.practicalHadoop.lucene.spatial.geometry.BoundingBox;
import com.practicalHadoop.lucene.spatial.geometry.GeometricShape;
import com.practicalHadoop.lucene.spatial.geometry.GeometryType;
import com.practicalHadoop.lucene.spatial.geometry.Point;

public class BoundingBoxBoundingBoxCustomFilter{

	private static final long serialVersionUID = 1L;
	private BoundingBox _box = null;
	
	public BoundingBoxBoundingBoxCustomFilter(BoundingBox b){
		_box = b;
	}

	public TopDocs FilterDocuments(IndexReader reader, TopDocs set) throws IOException {

		ScoreDoc[] original = set.scoreDocs;
		List<ScoreDoc> filtered = new LinkedList<ScoreDoc>();
		float maxScore = -1;
		for (ScoreDoc d : original) {
			Document doc = reader.document(d.doc);
			if(doc == null)
				continue;
			GeometricShape shape = GeometricShapeHelper.getGeometry(doc);
			if((shape == null) || (!shape.getType().equals(GeometryType.BOUNDINGBOX)))
				continue;
			if(_box.intersects((BoundingBox)shape)){
				filtered.add(d);
				if(d.score > maxScore)
					maxScore = d.score;
			}
		}
		ScoreDoc[] resSet = (ScoreDoc[])filtered.toArray(new ScoreDoc[filtered.size()]); 
		int hits = set.totalHits - (original.length - resSet.length);
		return new TopDocs(hits, resSet, maxScore);
	}
}
