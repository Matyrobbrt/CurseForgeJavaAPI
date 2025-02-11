package io.github.matyrobbrt.curseforgeapi.request.query;

import io.github.matyrobbrt.curseforgeapi.request.Arguments;

public interface PaginatedQuery<T extends PaginatedQuery<T>> extends Query {
    /**
     * A zero based index of the first item to include in the response
     *
     * @param index the index of the first item
     */
    T index(final int index);

    /**
     * The number of items to include in the response
     *
     * @param pageSize the number of items to include in the response
     */
    T pageSize(final int pageSize);

    /**
     * Paginate this query with the given {@code pagination}.
     */
    T paginated(PaginationQuery pagination);
}

class PaginatedImpl<T extends PaginatedImpl<T>> implements PaginatedQuery<T> {
    private Integer index;
    private Integer pageSize;

    /**
     * {@inheritDoc}
     */
    public T index(final int index) {
        this.index = index;
        return (T) this;
    }

    /**
     * {@inheritDoc}
     */
    public T pageSize(final int pageSize) {
        this.pageSize = pageSize;
        return (T) this;
    }

    /**
     * {@inheritDoc}
     */
    public T paginated(PaginationQuery pagination) {
        this.index = pagination.index;
        this.pageSize = pagination.pageSize;
        return (T) this;
    }

    @Override
    public Arguments toArgs() {
        return Arguments.of("index", index)
                .put("pageSize", pageSize);
    }
}
