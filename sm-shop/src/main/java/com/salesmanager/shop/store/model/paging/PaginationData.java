/**
 * 
 */
package com.salesmanager.shop.store.model.paging;

import java.io.Serializable;

/**
 *  POJO representation of pagination
 * @author Umesh Awasthi
 *
 */
public class PaginationData implements Serializable
{
    
    
    private static final long serialVersionUID = 1L;

    /** The number of results per page.*/
    private int pageSize;
    private int currentPage;
    private int offset ;
    private int totalCount;
    private int totalPages;
    private int countByPage;

    
    public PaginationData(int pageSize,int currentPage) {
								System.out.println("$#15187#"); if (pageSize == 0)
            throw new IllegalArgumentException("limit cannot be 0 for pagination.");

       
        this.pageSize = pageSize;
        this.currentPage=currentPage;
    }
    
    
    public int getPageSize()
    {
								System.out.println("$#15188#"); return pageSize;
    }

    
    /**
    * The current page number this pagination object represents
    *
    * @return the page number
    */
    public int getPageNumber() {
	    System.out.println("$#15190#");
	    System.out.println("$#15189#");
	    if (offset < pageSize || pageSize == 0) {
		    System.out.println("$#15192#");
		    return 1;
	    }

	    System.out.println("$#15195#");
	    System.out.println("$#15194#");
	    System.out.println("$#15193#");
	    return (offset / pageSize) + 1;
    }
    
    
    /**
    * The offset for this pagination object. The offset determines what index (0 index) to start retrieving results from.
    *
    * @return the offset
    */
        public int getOffset() {
												System.out.println("$#15199#"); System.out.println("$#15198#"); System.out.println("$#15197#"); System.out.println("$#15196#"); return (currentPage - 1) * pageSize + 1;
        }
        
     
    /**
     * Creates a new pagination object representing the next page
     * 
     * @return new pagination object with offset shifted by offset+limit
     */
    public PaginationData getNext()
    {
								System.out.println("$#15201#"); System.out.println("$#15200#"); return new PaginationData( offset + pageSize, pageSize );
    }
    
    
    /**
    * Creates a new pagination object representing the previous page
    *
    * @return new pagination object with offset shifted by offset-limit
    */
        public PaginationData getPrevious() {
												System.out.println("$#15203#"); System.out.println("$#15202#"); if (pageSize >= offset) {
																System.out.println("$#15204#"); return new PaginationData(0, pageSize);
            } else {
																System.out.println("$#15206#"); System.out.println("$#15205#"); return new PaginationData(offset - pageSize, pageSize);
            }
        }


    public int getCurrentPage()
    {
								System.out.println("$#15207#"); return currentPage;
    }


    public void setCurrentPage( int currentPage )
    {
        this.currentPage = currentPage;
    }


    public int getTotalCount()
    {
								System.out.println("$#15208#"); return totalCount;
    }


    public void setTotalCount( int totalCount )
    {
        this.totalCount = totalCount;
    }


    public int getTotalPages()
    {
        
								System.out.println("$#15209#"); Integer totalPages= Integer.valueOf((int) (Math.ceil(Integer.valueOf(totalCount).doubleValue() / pageSize)));
								System.out.println("$#15210#"); return totalPages;
    }


	public int getCountByPage() {
		System.out.println("$#15211#"); return countByPage;
	}


	public void setCountByPage(int countByPage) {
		this.countByPage = countByPage;
	}


	public void setTotalPages(int totalPages) {
		this.totalPages = totalPages;
	}

   
    
        
    
    
}
