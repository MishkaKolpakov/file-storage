# -*- mode: ruby -*-
# vi: set ft=ruby :

ENV['VAGRANT_DEFAULT_PROVIDER'] = 'virtualbox'

variables = ["POSTGRES_USER", "POSTGRES_PASSWORD", "POSTGRES_DB"]

variables.each do |var|
  if !ENV.has_key?(var)
    raise "Please specify the `#{var}` environment variable"
  end
end

$set_environment_variables = <<SCRIPT
tee "/etc/profile.d/myvars.sh" > "/dev/null" <<EOF
export POSTGRES_USER=#{ENV['POSTGRES_USER']}
export POSTGRES_PASSWORD=#{ENV['POSTGRES_PASSWORD']}
export POSTGRES_DB=#{ENV['POSTGRES_DB']}
EOF
SCRIPT

$init_script = <<SCRIPT
apt-get update -y
apt-get dist-upgrade -y
apt-get install curl git openjdk-8-jdk-headless maven -y
apt-get autoclean -y
apt-get autoremove -y
mvn clean package -Dmaven.test.skip=true -f /vagrant/pom.xml -B
SCRIPT

Vagrant.configure("2") do |config|
  config.vm.box = "ubuntu/xenial64"

  config.vm.provider "virtualbox" do |vb|
    # Display the VirtualBox GUI when booting the machine
    # Uncomment if you want to interact with it directly
    # vb.gui = true
    vb.cpus = 1
    vb.memory = "2048"
  end

  config.vm.network "private_network", ip: "192.168.200.100"
  # Uncomment if you want the application to be accessible via localhost
  # for convenience, as it is always accessible via private ip

  #config.vm.network "forwarded_port", guest: 8585, host: 9000
  #config.vm.network "forwarded_port", guest: 5432, host: 5432

  config.vm.provision "shell", inline: $set_environment_variables, run: "always"
  config.vm.provision "shell", inline: $init_script
  config.vm.provision "docker" do |d|
    d.pull_images "postgres:9.6"
    d.pull_images "java:8-jdk-alpine"
    d.build_image "/vagrant",
      args: "-t authservice"
    d.run "postgres", image: "postgres:9.6",
      args: "-p 5432:5432 -e POSTGRES_USER -e POSTGRES_PASSWORD -e POSTGRES_DB -v /opt/postgres:/var/lib/postgresql/data",
      restart: "always"
    d.run "authservice", image: "authservice",
      args: "-p 8585:8585 --link postgres:postgres -e POSTGRES_USER -e POSTGRES_PASSWORD -e POSTGRES_DB",
      restart: "always"
  end
end