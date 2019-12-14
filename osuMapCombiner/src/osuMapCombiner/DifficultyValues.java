package osuMapCombiner;

public enum DifficultyValues{
	
	HPDrainRate(1),
	CircleSize(2),
	OverallDifficulty(3),
	ApproachRate(4),
	SliderMultiplier(5),
	SliderTickRate(6),
	;
	
	
	
	int i=0;
	DifficultyValues(int i) {
		this.i=i;
	}
}