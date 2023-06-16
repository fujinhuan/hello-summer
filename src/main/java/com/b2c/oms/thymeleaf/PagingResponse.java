package com.b2c.oms.thymeleaf;

import java.util.List;

public class PagingResponse<T> {
    private int pageIndex;
    private int pageSize;
    private int totalSize;
    private int totalPage;
    private boolean isFirst;
    private boolean isEnd;
    private boolean hasNextPage;
    private boolean hasPrePage;
    private List<T> list;

    public PagingResponse(int pageIndex, int pageSize, int totalSize, List<T> list) {
        this.pageIndex = pageIndex == 0 ? 1 : pageIndex;
        this.pageSize = pageSize == 0 ? 10 : pageSize;
        this.totalSize = totalSize;
        if (totalSize == 0) this.totalPage = 0;
        else {
            this.totalPage = (totalSize % pageSize > 0) ? totalSize / pageSize + 1 : totalSize / pageSize;
        }
        this.isFirst = (pageIndex == 1);
        this.isEnd = (pageIndex == totalPage);
        this.hasNextPage = (totalPage > pageIndex);
        this.hasPrePage = (totalPage > 1 && pageIndex > 1);
        this.list = list;

    }

    public int getPageIndex() {
        return pageIndex;
    }

    public int getPageSize() {
        return pageSize;
    }

    public int getTotalSize() {
        return totalSize;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public boolean isFirst() {
        return isFirst;
    }

    public boolean isEnd() {
        return isEnd;
    }

    public boolean isHasNextPage() {
        return hasNextPage;
    }

    public boolean isHasPrePage() {
        return hasPrePage;
    }

    public List<T> getList() {
        return list;
    }
}
