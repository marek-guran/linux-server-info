using System;
using System.Collections.Generic;
using System.Text.Json.Serialization;

namespace Linux_Server_Info.Models
{
    public class ServerInfoModel
    {
        [JsonPropertyName("cpu")]
        public CpuInfo? Cpu { get; set; }

        [JsonPropertyName("storage")]
        public List<StorageInfo>? Storage { get; set; }

        [JsonPropertyName("ram")]
        public RamInfo? Ram { get; set; }

        [JsonPropertyName("network")]
        public Dictionary<string, NetworkInterfaceInfo>? Network { get; set; }

        [JsonPropertyName("os")]
        public OsInfo? Os { get; set; }
    }

    public class CpuInfo
    {
        [JsonPropertyName("type")]
        public string? Type { get; set; }

        [JsonPropertyName("architecture")]
        public string? Architecture { get; set; }

        [JsonPropertyName("architecture_type")]
        public string? ArchitectureType { get; set; }

        [JsonPropertyName("hardware")]
        public string? Hardware { get; set; }

        [JsonPropertyName("brand")]
        public string? Brand { get; set; }

        [JsonPropertyName("usage")]
        public string? Usage { get; set; }

        [JsonPropertyName("speed")]
        public string? Speed { get; set; }

        [JsonPropertyName("temperature")]
        public string? Temperature { get; set; }

        [JsonPropertyName("cores")]
        public Dictionary<string, string>? Cores { get; set; }
    }

    public class StorageInfo
    {
        [JsonPropertyName("name")]
        public string? Name { get; set; }

        [JsonPropertyName("size")]
        public string? Size { get; set; }

        [JsonPropertyName("type")]
        public string? Type { get; set; }

        [JsonPropertyName("mountpoint")]
        public string? Mountpoint { get; set; }

        [JsonPropertyName("fstype")]
        public string? Fstype { get; set; }

        [JsonPropertyName("used")]
        public string? Used { get; set; }

        [JsonPropertyName("free")]
        public string? Free { get; set; }

        [JsonPropertyName("usage_percent")]
        public string? UsagePercent { get; set; }
    }

    public class RamInfo
    {
        [JsonPropertyName("total")]
        public string? Total { get; set; }

        [JsonPropertyName("free")]
        public string? Free { get; set; }

        [JsonPropertyName("used")]
        public string? Used { get; set; }

        [JsonPropertyName("usage_percent")]
        public string? UsagePercent { get; set; }
    }

    public class NetworkInterfaceInfo
    {
        [JsonPropertyName("is_up")]
        public bool IsUp { get; set; }

        [JsonPropertyName("speed")]
        public string? Speed { get; set; }
    }

    public class OsInfo
    {
        [JsonPropertyName("name")]
        public string? Name { get; set; }

        [JsonPropertyName("distribution")]
        public string? Distribution { get; set; }

        [JsonPropertyName("kernel_version")]
        public string? KernelVersion { get; set; }

        [JsonPropertyName("uptime")]
        public string? Uptime { get; set; }
    }
}
