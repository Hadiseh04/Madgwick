extends Node

signal orientation_changed(pitch: float, roll: float, yaw: float)

var pitch := 0.0
var roll := 0.0
var yaw := 0.0
var orientation_bridge = null

func _ready() -> void:
	if Engine.has_singleton("OrientationBridge"):
		orientation_bridge = Engine.get_singleton("OrientationBridge")
		orientation_bridge.orientation_updated.connect(update_orientation)

func update_orientation(new_pitch: float, new_roll: float, new_yaw: float) -> void:
	pitch = new_pitch
	roll = new_roll
	yaw = new_yaw
	orientation_changed.emit(pitch, roll, yaw)
