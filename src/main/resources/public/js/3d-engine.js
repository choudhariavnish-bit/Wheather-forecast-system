/**
 * Real NASA Satellite 3D Earth Globe Engine
 * Ultra-photorealistic 3D Earth Globe with soft lighting, crystal-clear satellite imagery, zero glare dots, and majestic ultra-slow rotation.
 */

class RealEarth3DEngine {
    constructor(canvasId) {
        this.canvas = document.getElementById(canvasId);
        this.mouseX = 0;
        this.mouseY = 0;

        this.initThree();
        this.createStarfield();
        this.loadRealEarthGlobe();
        this.createLighting();

        this.animate = this.animate.bind(this);
        this.onWindowResize = this.onWindowResize.bind(this);
        this.onMouseMove = this.onMouseMove.bind(this);

        window.addEventListener('resize', this.onWindowResize);
        window.addEventListener('mousemove', this.onMouseMove);

        this.animate();
    }

    initThree() {
        this.scene = new THREE.Scene();
        this.scene.fog = new THREE.FogExp2(0x04060f, 0.008);

        this.camera = new THREE.PerspectiveCamera(52, window.innerWidth / window.innerHeight, 0.1, 1000);
        this.camera.position.set(0, 0, 36);

        this.renderer = new THREE.WebGLRenderer({
            canvas: this.canvas,
            antialias: true,
            alpha: true,
            powerPreference: "high-performance"
        });
        this.renderer.setSize(window.innerWidth, window.innerHeight);
        this.renderer.setPixelRatio(Math.min(window.devicePixelRatio, 2));
    }

    createStarfield() {
        const count = 3000;
        const geometry = new THREE.BufferGeometry();
        const positions = new Float32Array(count * 3);

        for (let i = 0; i < count; i++) {
            positions[i * 3] = (Math.random() - 0.5) * 350;
            positions[i * 3 + 1] = (Math.random() - 0.5) * 220;
            positions[i * 3 + 2] = (Math.random() - 0.5) * 220;
        }

        geometry.setAttribute('position', new THREE.BufferAttribute(positions, 3));

        const material = new THREE.PointsMaterial({
            color: 0xffffff,
            size: 0.8,
            transparent: true,
            opacity: 0.75,
            blending: THREE.AdditiveBlending
        });

        this.starfield = new THREE.Points(geometry, material);
        this.scene.add(this.starfield);
    }

    loadRealEarthGlobe() {
        this.earthGroup = new THREE.Group();
        const textureLoader = new THREE.TextureLoader();

        const earthMap = textureLoader.load('images/earth-satellite.jpg');
        const cloudMap = textureLoader.load('images/earth-clouds.png');

        // Large photorealistic Earth sphere radius
        this.earthRadius = 14;
        const geometry = new THREE.SphereGeometry(this.earthRadius, 64, 64);

        // Soft, non-glare MeshStandardMaterial for crystal-clear satellite imagery
        const earthMaterial = new THREE.MeshStandardMaterial({
            map: earthMap,
            roughness: 0.7,
            metalness: 0.1
        });

        this.earthMesh = new THREE.Mesh(geometry, earthMaterial);
        this.earthGroup.add(this.earthMesh);

        // Volumetric Cloud Layer
        const cloudGeometry = new THREE.SphereGeometry(this.earthRadius + 0.2, 64, 64);
        const cloudMaterial = new THREE.MeshStandardMaterial({
            map: cloudMap,
            transparent: true,
            opacity: 0.45,
            blending: THREE.AdditiveBlending
        });

        this.cloudMesh = new THREE.Mesh(cloudGeometry, cloudMaterial);
        this.earthGroup.add(this.cloudMesh);

        // Subtle Atmospheric Blue Rim Glow
        const atmosphereGeometry = new THREE.SphereGeometry(this.earthRadius + 0.8, 64, 64);
        const atmosphereMaterial = new THREE.MeshBasicMaterial({
            color: 0x38bdf8,
            transparent: true,
            opacity: 0.14,
            blending: THREE.AdditiveBlending,
            side: THREE.BackSide
        });

        this.atmosphereMesh = new THREE.Mesh(atmosphereGeometry, atmosphereMaterial);
        this.earthGroup.add(this.atmosphereMesh);

        // Position Earth framed on the RIGHT side of the screen
        this.earthGroup.position.set(13, 0, -2);
        this.scene.add(this.earthGroup);
    }

    createLighting() {
        // Soft uniform ambient light to eliminate harsh shadows and glare dots
        const ambientLight = new THREE.AmbientLight(0xffffff, 1.25);
        this.scene.add(ambientLight);

        // Soft directional sunlight
        const sunLight = new THREE.DirectionalLight(0xffffff, 0.75);
        sunLight.position.set(40, 20, 30);
        this.scene.add(sunLight);
    }

    onMouseMove(event) {
        this.mouseX = (event.clientX / window.innerWidth) * 2 - 1;
        this.mouseY = -(event.clientY / window.innerHeight) * 2 + 1;
    }

    animate() {
        requestAnimationFrame(this.animate);

        // Ultra-slow majestic Earth rotation (0.0008 rad/frame)
        if (this.earthMesh) {
            this.earthMesh.rotation.y += 0.0008;
        }

        // Ultra-slow cloud rotation
        if (this.cloudMesh) {
            this.cloudMesh.rotation.y += 0.0012;
        }

        // Starfield drift
        if (this.starfield) {
            this.starfield.rotation.y += 0.0002;
        }

        // Gentle Mouse Orbit Reactivity
        this.camera.position.x += (this.mouseX * 1.5 - this.camera.position.x) * 0.015;
        this.camera.position.y += (this.mouseY * 1.5 - this.camera.position.y) * 0.015;
        this.camera.lookAt(0, 0, 0);

        this.renderer.render(this.scene, this.camera);
    }

    onWindowResize() {
        this.camera.aspect = window.innerWidth / window.innerHeight;
        this.camera.updateProjectionMatrix();
        this.renderer.setSize(window.innerWidth, window.innerHeight);
    }
}

document.addEventListener('DOMContentLoaded', () => {
    window.realEarthEngine = new RealEarth3DEngine('bg-3d-canvas');
});
