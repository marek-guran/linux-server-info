package com.marekguran.serverinfo.ui

import org.simpleframework.xml.Element
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root

@Root(name = "system_information")
data class SystemInfo(
    @field:Element(name = "cpu")
    var cpu: CpuInfo? = null,

    @field:ElementList(inline = true, required = false)
    var storage: List<StorageDevice>? = null,

    @field:Element(name = "ram")
    var ram: RamInfo? = null,

    @field:ElementList(name = "network", inline = true, required = false)
    var network: List<NetworkInterface>? = null,

    @field:Element(name = "os")
    var os: OsInfo? = null
)

@Root(name = "cpu")
data class CpuInfo(
    @field:Element(name = "usage")
    var usage: String = "",

    @field:Element(name = "speed")
    var speed: String = "",

    @field:Element(name = "temperature")
    var temperature: String = "",

    @field:ElementList(inline = true, required = false)
    var cores: List<CpuCore>? = null
)

@Root(name = "core_1")
data class CpuCore(
    @field:Element(name = "usage")
    var usage: String = ""
)

@Root(name = "storage")
data class StorageDevice(
    @field:Element(name = "name")
    var name: String = "",

    @field:Element(name = "size")
    var size: String = "",

    @field:Element(name = "type")
    var type: String = "",

    @field:Element(name = "mountpoint")
    var mountpoint: String = "",

    @field:Element(name = "fstype")
    var fstype: String = "",

    @field:Element(name = "used")
    var used: String = "",

    @field:Element(name = "free")
    var free: String = "",

    @field:Element(name = "usage_percent")
    var usagePercent: String = ""
)

@Root(name = "ram")
data class RamInfo(
    @field:Element(name = "total")
    var total: String = "",

    @field:Element(name = "free")
    var free: String = "",

    @field:Element(name = "used")
    var used: String = "",

    @field:Element(name = "usage_percent")
    var usagePercent: String = ""
)

@Root(name = "interface")
data class NetworkInterface(
    @field:Element(name = "name")
    var name: String = "",

    @field:Element(name = "is_up")
    var isUp: String = "",

    @field:Element(name = "duplex")
    var duplex: String = "",

    @field:Element(name = "speed")
    var speed: String = ""
)

@Root(name = "os")
data class OsInfo(
    @field:Element(name = "name")
    var name: String = "",

    @field:Element(name = "distribution")
    var distribution: String = "",

    @field:Element(name = "kernel_version")
    var kernelVersion: String = "",

    @field:Element(name = "uptime")
    var uptime: String = ""
)
