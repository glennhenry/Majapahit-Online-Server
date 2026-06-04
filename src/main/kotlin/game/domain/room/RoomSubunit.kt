package game.domain.room

import encore.subunit.Subunit
import encore.subunit.scope.ServerScope
import encore.venue.Venue
import game.routes.models.pio.common.ServerEndpoint
import game.routes.models.pio.room.CreateRoomArgs
import game.routes.models.pio.room.CreateRoomOutput
import game.routes.models.pio.room.JoinRoomArgs
import game.routes.models.pio.room.JoinRoomOutput
import game.routes.models.pio.room.ListRoomsArgs
import game.routes.models.pio.room.ListRoomsOutput
import game.routes.models.pio.room.RoomInfo

/**
 * Server subunit that implements the PlayerIO room system.
 *
 * - API 21 request to create room
 * - API 24 request to join room
 * - API 30 request to list rooms
 */
class RoomSubunit : Subunit<ServerScope> {
    private val rooms = mutableMapOf<String, RoomInfo>()

    fun createRoom(args: CreateRoomArgs): CreateRoomOutput {
        val newRoom = RoomInfo(
            id = args.roomId,
            roomType = args.roomType,
            onlineUsers = 1,
            roomData = args.roomData
        )
        rooms[args.roomId] = newRoom
        return CreateRoomOutput(newRoom.id)
    }

    fun joinRoom(args: JoinRoomArgs): JoinRoomOutput? {
        if (!rooms.containsKey(args.roomId)) {
            return null
        }

        return JoinRoomOutput(
            joinKey = JOIN_KEY,
            endpoints = listOf(
                ServerEndpoint(
                    address = Venue.encore.server.host,
                    port = Venue.encore.server.socketPort
                )
            )
        )
    }

    fun listRooms(args: ListRoomsArgs): ListRoomsOutput {
        val rooms = rooms.values
            .drop(args.resultOffset)
            .filter { info ->
                // (?) unsure the room type identifier for dev room
                val devRoomOk = !args.onlyDevRooms || info.roomType == "DevRoom"
                val roomTypeOk = info.roomType == args.roomType
                // (?) unsure how searchCriteria is given and how to match it
                val criteriaOk = args.searchCriteria.all { criteria ->
                    info.roomData.find { it.key == criteria.key }?.equals(criteria.value) == true
                }

                devRoomOk && roomTypeOk && criteriaOk
            }
            .take(args.resultLimit)

        return ListRoomsOutput(rooms)
    }

    companion object {
        const val JOIN_KEY = "MOKV-join-key"
    }

    override suspend fun debut(scope: ServerScope): Result<Unit> {
        return runCatching {}
    }

    override suspend fun disband(scope: ServerScope): Result<Unit> {
        return runCatching {}
    }
}
