package simpledb;

import java.util.*;

/**
 * SeqScan is an implementation of a sequential scan access method that reads
 * each tuple of a table in no particular order (e.g., as they are laid out on
 * disk).
 */
public class SeqScan implements DbIterator {

    private static final long serialVersionUID = 1L;

    /**
     * Creates a sequential scan over the specified table as a part of the
     * specified transaction.
     * 
     * @param tid
     *            The transaction this scan is running as a part of.
     * @param tableid
     *            the table to scan.
     * @param tableAlias
     *            the alias of this table (needed by the parser); the returned
     *            tupleDesc should have fields with name tableAlias.fieldName
     *            (note: this class is not responsible for handling a case where
     *            tableAlias or fieldName are null. It shouldn't crash if they
     *            are, but the resulting name can be null.fieldName,
     *            tableAlias.null, or null.null).
     */
    private TransactionId transId;
    private int tabId;
    private String alias;


    public SeqScan(TransactionId tid, int tableid, String tableAlias) {
        // some code goes here
	transId = tid;
	tabId = tableid;
	alias = tableAlias;
    }

    /**
     * @return
     *       return the table name of the table the operator scans. This should
     *       be the actual name of the table in the catalog of the database
     * */
    public String getTableName() {
        return null;
    }
    
    /**
     * @return Return the alias of the table this operator scans. 
     * */
    public String getAlias()
    {
        // some code goes here
        return alias;
    }

    /**
     * Reset the tableid, and tableAlias of this operator.
     * @param tableid
     *            the table to scan.
     * @param tableAlias
     *            the alias of this table (needed by the parser); the returned
     *            tupleDesc should have fields with name tableAlias.fieldName
     *            (note: this class is not responsible for handling a case where
     *            tableAlias or fieldName are null. It shouldn't crash if they
     *            are, but the resulting name can be null.fieldName,
     *            tableAlias.null, or null.null).
     */
    public void reset(int tableid, String tableAlias) {
        // some code goes here
	tabId = tableid;
	alias = tableAlias;
    }

    public SeqScan(TransactionId tid, int tableid) {
        this(tid, tableid, Database.getCatalog().getTableName(tableid));
    }

    private DbFileIterator tupleIterator;

    public void open() throws DbException, TransactionAbortedException {
        // some code goes here
	tupleIterator = Database.getCatalog().getDbFile(tabId).iterator(transId);
	tupleIterator.open();
	
    }

    /**
     * Returns the TupleDesc with field names from the underlying HeapFile,
     * prefixed with the tableAlias string from the constructor. This prefix
     * becomes useful when joining tables containing a field(s) with the same
     * name.
     * 
     * @return the TupleDesc with field names from the underlying HeapFile,
     *         prefixed with the tableAlias string from the constructor.
     */
    public TupleDesc getTupleDesc() {
        // some code goes here
        TupleDesc nonAliased = Database.getCatalog().getTupleDesc(tabId);
	Type[] types = new Type[nonAliased.numFields()];
	String[] aliasedNames  = new String[nonAliased.numFields()];
	for(int index = 0; index < nonAliased.numFields(); index++){
	    types[index] = nonAliased.getFieldType(index);
	    aliasedNames[index] = alias + "." +  nonAliased.getFieldName(index);
	}
	return new TupleDesc(types, aliasedNames);
    }

    public boolean hasNext() throws TransactionAbortedException, DbException {
        // some code goes here
	if(tupleIterator != null){
	    return tupleIterator.hasNext();
	}
	return false;
    }

    public Tuple next() throws NoSuchElementException,
            TransactionAbortedException, DbException {
        // some code goes here
	if(tupleIterator != null){
	    Tuple nextTup = tupleIterator.next();
 	    if(nextTup != null){
	   	return nextTup;
	    }
	}
	throw new NoSuchElementException("No more Tuples left");
    }

    public void close() {
        // some code goes here
	tupleIterator = null;
    }

    public void rewind() throws DbException, NoSuchElementException,
            TransactionAbortedException {
        // some code goes here
	close();
	open();
    }
}

