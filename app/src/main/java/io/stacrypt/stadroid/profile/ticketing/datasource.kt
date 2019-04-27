package io.stacrypt.stadroid.data

/**
 * A simple data source factory which also provides a way to observe the last created data source.
 * This allows us to channel its network request status etc back to the UI. See the Listing creation
 * in the Repository class.
 */
class TicketListDataSourceFactory : ListingDataSourceFactory<Ticket>() {
    override suspend fun load(page: Int, pageSize: Int) = stemeraldApiClient.getTickets(
        take = pageSize,
        skip = page * pageSize
    ).await()
}

class TicketMessageDataSourceFactory(private val ticketId: Int) : ListingDataSourceFactory<TicketMessage>() {
    override suspend fun load(page: Int, pageSize: Int) = stemeraldApiClient.getTicketMessages(
        ticketId = ticketId,
        take = pageSize,
        skip = page * pageSize
    ).await()
}
